#!groovy
import jenkins.model.*
import hudson.security.*

def instance = Jenkins.getInstance()
def jenkins_username = System.getenv('JENKINS_USERNAME') ?: "admin"
def jenkins_password = System.getenv('JENKINS_PASSWORD') ?: "admin&admin1"

def hudsonRealm = new HudsonPrivateSecurityRealm(false)
hudsonRealm.createAccount(jenkins_username,jenkins_password)
instance.setSecurityRealm(hudsonRealm)

def strategy = new FullControlOnceLoggedInAuthorizationStrategy()
strategy.setAllowAnonymousRead(false)
instance.setAuthorizationStrategy(strategy)
instance.save()
