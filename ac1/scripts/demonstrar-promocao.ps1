param([string]$UrlBase = "http://localhost:8080")
$ErrorActionPreference = "Stop"
$UrlBase = $UrlBase.TrimEnd('/')

function Enviar-Json([string]$Caminho, [hashtable]$Dados) {
    Invoke-RestMethod -Method Post -Uri "$UrlBase$Caminho" `
        -ContentType "application/json; charset=utf-8" -Body ($Dados | ConvertTo-Json)
}

$aluno = Enviar-Json "/api/alunos" @{ nome = "Aluno demonstracao US01" }
Write-Host "Aluno criado: ID $($aluno.id), plano $($aluno.plano)"
for ($numero = 1; $numero -le 11; $numero++) {
    $resultado = Enviar-Json "/api/alunos/$($aluno.id)/conclusoes" @{ codigoCurso = "CURSO-$numero"; media = 8.0 }
}
Write-Host "Antes da promocao: $($resultado.aluno.cursosConcluidos) cursos, $($resultado.aluno.plano)"
$resultado = Enviar-Json "/api/alunos/$($aluno.id)/conclusoes" @{ codigoCurso = "CURSO-12"; media = 6.99 }
Write-Host "Media 6,99: $($resultado.aluno.cursosConcluidos) cursos, $($resultado.aluno.plano)"
$resultado = Enviar-Json "/api/alunos/$($aluno.id)/conclusoes" @{ codigoCurso = "CURSO-12"; media = 7.0 }
$resultado | ConvertTo-Json -Depth 5
if ($resultado.aluno.plano -ne "PREMIUM" -or $resultado.aluno.cursosConcluidos -ne 12 -or !$resultado.promovidoAgora) {
    throw "A promocao nao apresentou o resultado esperado."
}
Write-Host "Consulte este aluno na tela Vue, no Swagger e no banco para registrar as evidencias."
