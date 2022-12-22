pipeline {
    agent {
        label 'agent-1'
    }

    environment {
        DOCKERHUB_IMAGE = 'mdd13/jenkins-medaev'
        DOCKERHUB_TAG = 'latest'
    }

    stages { 
        stage ('Build Autossh Image') {
            steps {
                withCredentials([sshUserPrivateKey(credentialsId: 'ssh_key_image', keyFileVariable: 'SSH_KEY_IMAGE')]) {
                    sh('''
                    set +x
                    sudo docker build -t "$DOCKERHUB_IMAGE:$DOCKERHUB_TAG" -f autossh-with-envs.dockerfile --build-arg SSH_PRV_KEY="$(cat $SSH_KEY_IMAGE)" .
                    ''')
                }
            }
        }

        stage('Push Autossh Image') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'docker', usernameVariable: 'login', passwordVariable: 'password')]) {
                    sh('''
                    sudo docker login -u $login -p $password
                    sudo docker push $DOCKERHUB_IMAGE:$DOCKERHUB_TAG
                    ''')
                }
            }
        }
    }

    post { 
        always { 
            cleanWs()
        }
    }
}
