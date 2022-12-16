pipeline {
    agent any
    environment {
        GITHUB_REPO = 'https://github.com/exitfound/docker-autossh'
        DOCKERHUB_REPO = 'mdd13/jenkins-medaev'
        REMOTE_HOST = '192.168.88.218'
    }

    stages {
        stage('Parallel') {
            stage ('OpenSUSE Install') {
                steps {
                    cleanWs()
                    sh('''
                        sudo zypper install -y git docker
                        sudo git clone $GITHUB_REPO
                    ''')
                }
            }
        }

    }
}
