<script setup>
import { computed, onMounted, ref, watch } from 'vue';

const alunos = ref([]);
const historicos = ref({});
const alunoId = ref('');
const nome = ref('');
const codigoCurso = ref('');
const media = ref('');
const ocupado = ref(false);
const erro = ref('');
const mensagem = ref('');
const alunoSelecionado = computed(() => alunos.value.find(aluno => aluno.id === Number(alunoId.value)));
const cursosRestantes = computed(() => Math.max(0, 12 - (alunoSelecionado.value?.cursosConcluidos ?? 0)));
const progresso = computed(() => Math.min(100, (alunoSelecionado.value?.cursosConcluidos ?? 0) / 12 * 100));

async function carregarHistorico(id) {
  historicos.value[id] = await requisitar(`/alunos/${id}/recompensas`);
}
watch(alunoId, id => {
  if (id) carregarHistorico(id).catch(falha => { if (alunoId.value === id) erro.value = falha.message; });
});

async function requisitar(caminho, corpo) {
  const resposta = await fetch(`/api${caminho}`, corpo === undefined ? {} : {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(corpo),
  });
  let dados;
  try { dados = await resposta.json(); }
  catch { throw new Error('Não foi possível ler a resposta do servidor. Verifique se a aplicação está disponível.'); }
  if (!resposta.ok) throw new Error(dados.detail || 'Não foi possível concluir a operação.');
  return dados;
}

async function executar(acao) {
  ocupado.value = true;
  erro.value = '';
  mensagem.value = '';
  try { await acao(); }
  catch (falha) { erro.value = falha instanceof TypeError ? 'Não foi possível conectar à aplicação. Tente novamente.' : falha.message; }
  finally { ocupado.value = false; }
}

async function carregarAlunos() {
  await executar(async () => {
    alunos.value = await requisitar('/alunos');
    if (!alunos.value.some(aluno => aluno.id === Number(alunoId.value))) {
      alunoId.value = alunos.value[0]?.id ?? '';
    }
  });
}

async function cadastrar() {
  await executar(async () => {
    const aluno = await requisitar('/alunos', { nome: nome.value.trim() });
    alunos.value.push(aluno);
    alunoId.value = aluno.id;
    nome.value = '';
    mensagem.value = 'Aluno cadastrado no plano Básico.';
  });
}

async function concluirCurso() {
  const idAtual = alunoSelecionado.value?.id;
  if (idAtual === undefined) return;
  await executar(async () => {
    const resultado = await requisitar(`/alunos/${idAtual}/conclusoes`, {
      codigoCurso: codigoCurso.value.trim(),
      media: Number(media.value),
    });
    alunos.value = alunos.value.map(aluno => aluno.id === idAtual ? resultado.aluno : aluno);
    await carregarHistorico(idAtual);
    if (resultado.promovidoAgora) mensagem.value = '12 cursos válidos concluídos. O aluno agora é Premium! Recebeu 1 voucher e 3 moedas.';
    else if (resultado.cursoContabilizado) mensagem.value = 'Curso contabilizado. A progressão do aluno foi atualizada.';
    else mensagem.value = 'Média inferior a 7,0: o curso não foi contabilizado para a progressão.';
    if (resultado.cursoContabilizado) {
      codigoCurso.value = '';
      media.value = '';
    }
  });
}

function trocarAluno() {
  codigoCurso.value = '';
  media.value = '';
  erro.value = '';
  mensagem.value = '';
}

onMounted(carregarAlunos);
</script>

<template>
  <div class="pagina">
    <header class="cabecalho">
      <a class="marca" href="/" aria-label="Educação Continuada, início"><span class="simbolo">EC</span> Educação Continuada</a>
      <span class="etiqueta">Progressão do aluno</span>
    </header>

    <main :aria-busy="ocupado">
      <div class="abertura">
        <p class="sobretitulo">CADA CONCLUSÃO É UM NOVO PASSO</p>
        <h1>Seu aprendizado,<br>em evolução.</h1>
        <p>Conclua 12 cursos com média a partir de 7,0 para alcançar o plano Premium.</p>
      </div>

      <div v-if="erro" class="aviso erro" role="alert">{{ erro }}</div>
      <div v-if="mensagem" class="aviso sucesso" role="status">{{ mensagem }}</div>

      <div class="grade">
        <aside class="painel">
          <p class="sobretitulo">COMECE POR AQUI</p>
          <h2>Alunos</h2>
          <form @submit.prevent="cadastrar">
            <label for="nome">Nome do novo aluno</label>
            <input id="nome" v-model="nome" maxlength="120" required placeholder="Nome completo" :disabled="ocupado">
            <button type="submit" :disabled="ocupado || !nome.trim()">Cadastrar aluno</button>
          </form>
          <div class="divisor"></div>
          <label for="aluno">Consultar progressão</label>
          <select id="aluno" v-model="alunoId" :disabled="ocupado || !alunos.length" @change="trocarAluno">
            <option value="" disabled>Selecione um aluno</option>
            <option v-for="aluno in alunos" :key="aluno.id" :value="aluno.id">{{ aluno.nome }} · #{{ aluno.id }}</option>
          </select>
          <button class="botao-texto" type="button" @click="carregarAlunos" :disabled="ocupado">{{ ocupado ? 'Aguarde…' : 'Atualizar lista' }}</button>
        </aside>

        <section v-if="alunoSelecionado" class="painel principal" aria-labelledby="titulo-progresso">
          <div class="linha-titulo">
            <div><p class="sobretitulo">TRAJETÓRIA DE APRENDIZADO</p><h2 id="titulo-progresso">{{ alunoSelecionado.nome }}</h2></div>
            <span class="plano" :class="{ premium: alunoSelecionado.plano === 'PREMIUM' }">{{ alunoSelecionado.plano === 'PREMIUM' ? 'Premium' : 'Básico' }}</span>
          </div>

          <div class="contagem"><strong>{{ alunoSelecionado.cursosConcluidos }}</strong><span>cursos válidos concluídos</span></div>
          <div class="barra" role="progressbar" :aria-valuenow="Math.min(alunoSelecionado.cursosConcluidos, 12)" aria-valuemin="0" aria-valuemax="12" aria-label="Progresso para o plano Premium"><span :style="{ width: `${progresso}%` }"></span></div>
          <p class="legenda">{{ alunoSelecionado.plano === 'PREMIUM' ? 'Meta alcançada. Status atualizado para Premium.' : `Faltam ${cursosRestantes} cursos válidos para o plano Premium.` }}</p>

          <div class="divisor"></div>
          <section aria-labelledby="titulo-carteira" class="carteira">
            <h3 id="titulo-carteira">Carteira de recompensas</h3>
            <p class="ajuda">{{ alunoSelecionado.plano === 'PREMIUM' ? 'Carteira habilitada. Prêmios recebidos pela sua evolução.' : 'Conquiste o plano Premium para habilitar sua carteira.' }}</p>
            <div class="premios">
              <div><strong aria-label="Vouchers recebidos">{{ alunoSelecionado.vouchers }}</strong><span>voucher de projeto real</span></div>
              <div><strong aria-label="Moedas recebidas">{{ alunoSelecionado.moedas }}</strong><span>moedas virtuais</span></div>
            </div>
            <ul v-if="historicos[alunoSelecionado.id]?.length" class="historico">
              <li v-for="registro in historicos[alunoSelecionado.id]" :key="registro.alunoId">
                <strong>Promoção ao Premium</strong>
                <span>+{{ registro.vouchers }} voucher · +{{ registro.moedas }} moedas</span>
                <time :datetime="registro.registradoEm">{{ new Date(registro.registradoEm).toLocaleString('pt-BR') }}</time>
              </li>
            </ul>
          </section>
          <div class="divisor"></div>
          <h3>Registrar conclusão</h3>
          <p class="ajuda">Informe o código do curso concluído e a média final. Cada curso conta uma única vez por aluno.</p>
          <form @submit.prevent="concluirCurso">
            <div class="campos">
              <div><label for="curso">Código do curso</label><input id="curso" v-model="codigoCurso" maxlength="80" required placeholder="Ex.: JAVA-01" :disabled="ocupado"></div>
              <div><label for="media">Média final</label><input id="media" v-model="media" type="number" min="0" max="10" step="0.01" required placeholder="0 a 10" :disabled="ocupado"></div>
            </div>
            <button type="submit" :disabled="ocupado || !codigoCurso.trim() || media === ''">Registrar conclusão</button>
          </form>
        </section>

        <section v-else class="painel vazio">
          <span class="icone-vazio" aria-hidden="true">01</span>
          <h2>Uma nova trajetória começa aqui.</h2>
          <p>Cadastre um aluno para acompanhar os cursos concluídos e a evolução do plano.</p>
        </section>
      </div>
      <p class="nota">Somente cursos com média igual ou superior a 7,0 entram na contagem para a promoção.</p>
    </main>
    <footer>Educação Continuada Gamificada · US01 e US02</footer>
  </div>
</template>
