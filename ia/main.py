from fastapi import FastAPI
app = FastAPI()
@app.get('/recomendacoes')
def recomendar():
    # Contrato de fronteira: fixture deterministica, nao modelo treinado.
    return {'origem': 'python', 'cursos': ['Testes com Mockito']}
@app.get('/health')
def health(): return {'status': 'up'}
