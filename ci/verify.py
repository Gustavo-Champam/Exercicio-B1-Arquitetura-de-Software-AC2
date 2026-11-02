import json,time,urllib.request,subprocess
from pathlib import Path
out=Path('evidence');out.mkdir(exist_ok=True)
def request(url,data=None):
 req=urllib.request.Request(url,data=None if data is None else json.dumps(data).encode(),headers={'Content-Type':'application/json'})
 with urllib.request.urlopen(req,timeout=8) as r:return json.load(r)
def ready(url):
 for _ in range(90):
  try:return request(url)
  except Exception:time.sleep(2)
 raise RuntimeError('Nao iniciou: '+url)
ready('http://localhost:8084/actuator/health');ready('http://localhost:8083/actuator/health')
aluno=request('http://localhost:8084/api/alunos',{'nome':'Evidencia B1'})
for n in range(1,13):request(f"http://localhost:8084/api/alunos/{aluno['id']}/conclusoes",{'codigoCurso':f'CURSO-{n}','media':8.0})
ranking=request('http://localhost:8084/api/ranking');assert next(p for p in ranking if p['id']==aluno['id'])['pontos']==1203
online=request('http://localhost:8084/api/recomendacoes');assert online['origem']=='python'
subprocess.run(['docker','compose','stop','ia'],check=True)
offline=request('http://localhost:8084/api/recomendacoes');assert offline['origem']=='catalogo'
jogos=[{'id':1,'data':'2026-10-20','resultados':[{'participante':'Ana','pontos':20},{'participante':'Bia','pontos':10}]}]
assert request('http://localhost:8083/jogos/finalizar?hoje=2026-10-26',jogos)==1
time.sleep(15)
queries={}
for port,query in [(9094,'ia_fallback_total'),(9093,'jogos_finalizados_total')]:
 result=request(f'http://localhost:{port}/api/v1/query?query='+query);assert result['data']['result'];assert float(result['data']['result'][0]['value'][1])>=1;queries[query]=result
(out/'validacao.json').write_text(json.dumps({'ranking':ranking,'ia_online':online,'ia_offline':offline,'metricas':queries,'sms_email':'adapters simulados; nenhuma mensagem real enviada'},indent=2))
print('AC1 ranking 1203; IA online/offline; A3 persistencia e avisos simulados; metricas coletadas: OK')
