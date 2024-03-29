pipeline {

    agent {
        label 'agent-1'
    }

    environment {
        DOCKERHUB_IMAGE = 'mdd13/jenkins-medaev'
        DOCKERHUB_TAG = 'latest'
    }

    parameters {
        // Параметры для подключения, если мы хотим организовать туннель на удаленном узле:
        string(name: 'SSH_USER', defaultValue: '', trim: true, description: 'Имя пользователя, которое будет использоваться для подключения к удаленному серверу:')
        string(name: 'SSH_HOST', defaultValue: '', trim: true, description: 'IP-адрес, который будет использоваться для подключения к удаленному серверу:')

        // Параметры для подключения к конечному серверу, откуда будет создаваться туннель:
        string(name: 'SSH_TUNNEL_USER', defaultValue: '', trim: true, description: 'Имя пользователя, которое будет использоваться для подключения к конечному серверу при создании туннеля:')
        string(name: 'SSH_TUNNEL_HOST', defaultValue: '', trim: true, description: 'IP-адрес, который будет использоваться для подключения к конечному серверу при создании туннеля:')
        string(name: 'SSH_TUNNEL_PORT', defaultValue: '22', trim: true, description: 'Порт SSH для подключения к конечному серверу при создании туннеля:')

        // Параметры для настройки туннеля:
        string(name: 'SSH_TUNNEL_MODE', defaultValue: '-L', trim: true, description: 'Режим, в котором будет работать туннель (прямой или обратный). По умолчанию используется прямой:')        
        string(name: 'SSH_TUNNEL_LOCALPORT', defaultValue: '9090', trim: true, description: 'Локальный порт сервиса, проброшенный с конечного инстанса, который будет запущен на вашем инстансе:')
        string(name: 'SSH_TUNNEL_IP', defaultValue: '', trim: true, description: 'IP-адрес конечного инстанса, до которого будет прокинут туннель:')
        string(name: 'SSH_TUNNEL_REMOTEPORT', defaultValue: '80', trim: true, description: 'Удаленный порт сервиса, запущенный на конечном инстансе и это тот порт, который активен на этом инстансе:')
    }

    stages {
        stage ('Build Autossh Image') {
            steps {
                withCredentials([sshUserPrivateKey(credentialsId: 'ssh_key_image', keyFileVariable: 'SSH_KEY_IMAGE')]) {
                    sh('''
                    set +x
                    sudo docker build -t "$DOCKERHUB_IMAGE:$DOCKERHUB_TAG" -f autossh.dockerfile --build-arg SSH_PRV_KEY="$(cat $SSH_KEY_IMAGE)" .
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

        stage ('Run Autossh Image') {
            steps {
                withCredentials([sshUserPrivateKey(credentialsId: 'ssh_key_host', keyFileVariable: 'SSH_KEY_HOST')]){
                    sh '''
                    ssh -i $SSH_KEY_HOST -o StrictHostKeyChecking=no ${SSH_USER}@${SSH_HOST} "sudo docker pull $DOCKERHUB_IMAGE:$DOCKERHUB_TAG"
                    ssh -i $SSH_KEY_HOST -o StrictHostKeyChecking=no ${SSH_USER}@${SSH_HOST} "sudo docker rm -f autossh && sudo docker run -d \
                    -e SSH_TUNNEL_PORT="${SSH_TUNNEL_PORT}" \
                    -e SSH_TUNNEL_MODE="${SSH_TUNNEL_MODE}" \
                    -e SSH_TUNNEL_LOCALPORT="${SSH_TUNNEL_LOCALPORT}" \
                    -e SSH_TUNNEL_IP="${SSH_TUNNEL_IP}" \
                    -e SSH_TUNNEL_REMOTEPORT="${SSH_TUNNEL_REMOTEPORT}" \
                    -e SSH_TUNNEL_USER="${SSH_TUNNEL_USER}" \
                    -e SSH_TUNNEL_HOST="${SSH_TUNNEL_HOST}" \
                    --name autossh \
                    --network host \
                    --restart unless-stopped \
                    $DOCKERHUB_IMAGE:$DOCKERHUB_TAG"
                    '''
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
