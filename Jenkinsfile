pipeline {
 agent any
 environment { JAVA_HOME = "${env.JAVA_HOME_17_X64}"; PATH = "${env.JAVA_HOME_17_X64}/bin:${env.PATH}"; TESTAR_POSTGRES = 'true'; DB_URL = 'jdbc:postgresql://localhost:15432/educacao'; DB_USER = 'educacao'; DB_PASSWORD = "${env.POSTGRES_PASSWORD}" }
 stages {
  stage('Preparar PostgreSQL real') { steps { sh 'docker run -d --name b1-postgres -p 127.0.0.1:15432:5432 -e POSTGRES_DB=educacao -e POSTGRES_USER=educacao -e POSTGRES_PASSWORD postgres:17'; sh 'for i in $(seq 1 60); do docker exec b1-postgres pg_isready -U educacao && exit 0; sleep 2; done; exit 1' } }
  stage('AC1 evoluida - testes e cobertura') { steps { dir('ac1') { sh 'chmod +x mvnw; ./mvnw -B clean verify' } } }
  stage('A3 - mocks integracao e cobertura') { steps { dir('A3_Mock_Observabilidade') { sh 'chmod +x mvnw; ./mvnw -B clean install' } } }
  stage('Microsolucoes eventos MQTT e IA') { steps { sh 'python3 -m venv .venv && .venv/bin/pip install -r ia/requirements.txt -r microsolucoes/requirements.txt'; sh 'mkdir -p evidence; .venv/bin/python microsolucoes/eventos.py > evidence/eventos.log 2>&1'; sh '.venv/bin/python microsolucoes/mqtt_lab.py > evidence/mqtt.log 2>&1' } }
  stage('Construir e implantar laboratorio') { steps { sh 'docker compose build'; sh 'docker compose up -d'; sh 'docker compose -p a3lab -f A3_Mock_Observabilidade/compose.yml up -d --build' } }
  stage('Validar APIs falha IA e metricas') { steps { sh 'python3 ci/verify.py' } }
 }
 post { always { junit 'ac1/target/surefire-reports/*.xml,A3_Mock_Observabilidade/target/surefire-reports/*.xml'; archiveArtifacts artifacts: 'ac1/target/*.jar,A3_Mock_Observabilidade/target/*.jar,evidence/*', fingerprint: true } }
}
