import {test, expect} from '@playwright/test';
class AlunoPage {
  constructor(page) { this.page = page; }
  async concluir(codigo, media) {
    await this.page.getByLabel('Código do curso').fill(codigo);
    await this.page.getByLabel('Média final').fill(String(media));
    await this.page.getByRole('button', {name:'Registrar conclusão',exact:true}).click();
  }
}
test('cadastro, nota insuficiente, promoção, carteira e persistência na tela', async ({page, request}) => {
  const tela=new AlunoPage(page);
  const nome=`Aluno demonstração ${Date.now()}`;
  await page.goto('/');
  await page.getByLabel('Nome do novo aluno').fill(nome);
  await page.getByRole('button',{name:'Cadastrar aluno'}).click();
  await expect(page.getByRole('heading',{name:nome})).toBeVisible();
  const id=Number(await page.getByLabel('Consultar progressão').inputValue());
  for(let i=1;i<=11;i++) {
    const r=await request.post(`/api/alunos/${id}/conclusoes`,{data:{codigoCurso:`E2E-${i}`,media:8}});
    expect(r.ok()).toBeTruthy();
  }
  await page.getByRole('button',{name:'Atualizar lista'}).click();
  await expect(page.getByRole('progressbar')).toHaveAttribute('aria-valuenow','11');
  await tela.concluir('E2E-12',6.99);
  await expect(page.getByRole('status')).toContainText('Média inferior');
  await tela.concluir('E2E-12',7);
  await expect(page.getByRole('status')).toContainText('Premium');
  await expect(page.getByLabel('Vouchers recebidos')).toHaveText('1');
  await expect(page.getByLabel('Moedas recebidas')).toHaveText('3');
  await expect(page.getByText('Promoção ao Premium',{exact:true})).toBeVisible();
  await tela.concluir('E2E-12',7);
  await expect(page.getByRole('alert')).toContainText('já foi contabilizado');
  await page.reload();
  await page.getByLabel('Consultar progressão').selectOption(String(id));
  await expect(page.getByLabel('Moedas recebidas')).toHaveText('3');
  await page.screenshot({path:'../evidencias/execucao/vue-desktop.png',fullPage:true});
  await page.setViewportSize({width:390,height:844});
  await page.screenshot({path:'../evidencias/execucao/vue-mobile.png',fullPage:true});
  expect(await page.evaluate(()=>document.documentElement.scrollWidth <= innerWidth)).toBeTruthy();
});
