"""Aceite HTTP real contra uma instancia dedicada de demonstracao."""
import json
import sys
import time
from datetime import datetime, timezone
from urllib.request import Request, urlopen
from urllib.error import HTTPError, URLError
from http.client import HTTPException
from pathlib import Path

base = sys.argv[1] if len(sys.argv) > 1 else "http://localhost:8080"
output = Path(sys.argv[2] if len(sys.argv) > 2 else "evidencias/execucao/http.json")

def call(path, data=None, expected=200):
    request = Request(base + path, data=None if data is None else json.dumps(data).encode(),
                      headers={"Content-Type": "application/json"})
    try:
        with urlopen(request, timeout=10) as response:
            status, body = response.status, response.read()
    except HTTPError as error:
        status, body = error.code, error.read()
    assert status == expected, (path, status, body)
    return json.loads(body)

for attempt in range(90):
    try:
        call("/api/alunos")
        break
    except (OSError, HTTPException, AssertionError):
        if attempt == 89:
            raise
        time.sleep(2)
aluno = call("/api/alunos", {"nome": "Demonstracao ATDD"}, 201)
id = aluno["id"]
assert aluno["vouchers"] == 0 and aluno["moedas"] == 0
for i in range(1, 12):
    result = call(f"/api/alunos/{id}/conclusoes", {"codigoCurso": f"C-{i}", "media": 8})
    assert result["aluno"]["plano"] == "BASICO"
reprovacao = call(f"/api/alunos/{id}/conclusoes", {"codigoCurso": "C-12", "media": 6.99})
assert reprovacao["aluno"]["cursosConcluidos"] == 11
promocao = call(f"/api/alunos/{id}/conclusoes", {"codigoCurso": "C-12", "media": 7})
assert promocao["promovidoAgora"] is True
call(f"/api/alunos/{id}/conclusoes", {"codigoCurso": " c-12 ", "media": 7}, 409)
call(f"/api/alunos/{id}/conclusoes", {"codigoCurso": "C-13", "media": 10})
final = call(f"/api/alunos/{id}")
assert (final["plano"], final["cursosConcluidos"], final["vouchers"], final["moedas"]) == ("PREMIUM", 13, 1, 3)
historico = call(f"/api/alunos/{id}/recompensas")
assert len(historico) == 1 and historico[0]["origem"] == "PROMOCAO_PREMIUM"
assert historico[0]["vouchers"] == 1 and historico[0]["moedas"] == 3
assert "/api/alunos/{id}/recompensas" in call("/v3/api-docs")["paths"]
output.parent.mkdir(parents=True, exist_ok=True)
output.write_text(json.dumps({"executadoEm": datetime.now(timezone.utc).isoformat(), "url": base,
    "resultado": "PASS", "notaInsuficiente": reprovacao, "promocao": promocao,
    "consultaFinal": final, "historico": historico}, indent=2, ensure_ascii=False), encoding="utf-8")
print(output.read_text(encoding="utf-8"))
