pipeline {
 agent any
 tools { jdk 'jdk17' }
 stages {
  stage('Core testar e empacotar') { steps { dir('core') { sh './mvnw -B clean verify' } } }
  stage('IA validar contrato') { steps { sh 'python3 -m venv .venv && .venv/bin/pip install -r ia/requirements.txt'; sh '.venv/bin/python -m py_compile ia/main.py' } }
  stage('Eventos testar recuperacao') { steps { sh '.venv/bin/python microsolucoes/eventos.py' } }
  stage('Construir imagens') { steps { sh 'docker compose build' } }
  stage('Implantar laboratorio') { steps { sh 'docker compose up -d' } }
  stage('Smoke test') { steps { sh 'curl --fail --retry 20 --retry-connrefused --retry-delay 3 http://localhost:8084/actuator/health' } }
 }
 post { always { junit 'core/target/surefire-reports/*.xml'; archiveArtifacts artifacts: 'core/target/*.jar', fingerprint: true } }
}
