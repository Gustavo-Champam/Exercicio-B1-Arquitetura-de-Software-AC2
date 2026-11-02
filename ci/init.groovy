import jenkins.model.Jenkins
import hudson.security.HudsonPrivateSecurityRealm
import hudson.security.FullControlOnceLoggedInAuthorizationStrategy
import org.jenkinsci.plugins.workflow.job.WorkflowJob
import org.jenkinsci.plugins.workflow.cps.CpsScmFlowDefinition
import hudson.plugins.git.GitSCM
import hudson.plugins.git.UserRemoteConfig
import hudson.plugins.git.BranchSpec

def j=Jenkins.get()
def realm=new HudsonPrivateSecurityRealm(false)
realm.createAccount('gustavo-a2',System.getenv('JENKINS_PASSWORD'))
j.setSecurityRealm(realm)
def auth=new FullControlOnceLoggedInAuthorizationStrategy()
auth.setAllowAnonymousRead(false)
j.setAuthorizationStrategy(auth)
j.setNumExecutors(1)
def job=j.createProject(WorkflowJob,'B1_A3_Equipe')
def scm=new GitSCM([new UserRemoteConfig(System.getenv('A2_REPO_URL'),null,null,null)],[new BranchSpec('*/main')],false,[],null,null,[])
job.setDefinition(new CpsScmFlowDefinition(scm,'Jenkinsfile'))
job.setDescription('B1 e A3: AC1 evoluida, mocks, JUnit, JaCoCo, microsolucoes e observabilidade. Ambiente temporario GitHub Actions.')
job.save();j.save()
