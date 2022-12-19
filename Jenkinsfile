pipeline {
    agent {
        label 'agent-1'
    }

    environment {
        DOCKERHUB_IMAGE = 'mdd13/jenkins-medaev'
        DOCKERHUB_TAG = 'latest'
    }

    // parameters {
    //     string(name: 'GITHUB_REPO', defaultValue: '', trim: true, description: 'Тэг для образа autossh:')
    // }

    stages { 
        stage ('Build') { 
            steps {
                withCredentials([sshUserPrivateKey(credentialsId: 'ssh_key_image', keyFileVariable: 'SSH_KEY_IMAGE')]) {
                    sh('''
                    set +x
                    sudo docker build -t "$DOCKERHUB_IMAGE:$DOCKERHUB_TAG" -f ./docker-autossh/autossh-with-envs.dockerfile --build-arg SSH_PRV_KEY="$(cat $SSH_KEY_IMAGE)" .
                    ''')
                }
            }
        }
    }
}
