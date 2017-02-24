node {

    SSH_PRIVATE = sh (
    script: 'cat /root/.ssh/id_rsa',
    returnStdout: true
    ).trim()
}

node( slaveName ) {
  stage('Checkout')
    sh 'mkdir -p /home/jenkins/.ssh/'
    sh 'ssh-keyscan -t rsa bitbucket.org >> ~/.ssh/known_hosts'
    writeFile file: '/home/jenkins/.ssh/id_rsa', text: SSH_PRIVATE
    sh 'chmod 600 /home/jenkins/.ssh/id_rsa'
    git branch: BRANCH_NAME , url: REPOSITORY_CLONE_ADDRESS

  stage('Docker Registry Login')
    sh '$(aws ecr get-login --region=us-east-1)'

  stage('Docker Build and Publish')
    parallel (
        "ken-service": {
            docker.withRegistry(REGISTRY_NAME){
                docker.build('ken:${BUILD_NUMBER}', '-f Dockerfile .')
                docker.image('ken:${BUILD_NUMBER}').push('v1.${BUILD_NUMBER}')

            }

        }
    )
  stage ('Complete')
      println 'Image successfully build and pushed to ecr'

}
