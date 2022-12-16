// pipeline {
//     agent none
//     environment {
//         GITHUB_REPO = 'https://github.com/exitfound/docker-autossh'
//         DOCKERHUB_REPO = 'mdd13/jenkins-medaev'
//         REMOTE_HOST = '192.168.88.218'
//     }

//     parameters {
//         string(name: 'IMAGE_TAG', defaultValue: 'latest', trim: true, description: 'Тэг для образа autossh:')
//         string(name: 'SSH_PORT', defaultValue: '22', trim: true, description: 'Порт SSH-сервера для подключения к даленному серверу:')
//         string(name: 'SSH_USER', defaultValue: '', trim: true, description: 'Имя пользователя, которое будет использоваться для подключения:')
//         string(name: 'SSH_HOST', defaultValue: '', trim: true, description: 'IP-адрес конечного инстанса, который будет использоваться для подключения:')
//         string(name: 'SSH_TUNNEL', defaultValue: '', trim: true, description: 'IP-адрес конечного инстанса, который будет использоваться для создания туннеля:')
//         string(name: 'SSH_LOCAL_PORT', defaultValue: '', trim: true, description: 'Локальный порт, который будет фигурировать на инстансе, где запущен контейнер:')
//         string(name: 'SSH_REMOTE_PORT', defaultValue: '', trim: true, description: 'Удаленный порт, который будет фигурировать на инстансе, к которому подключается контейнер:')
//     }

//     stages {
//         stage('Parallel Preinstall') {
//             parallel {
//                 stage ('OpenSUSE Install') {
//                     agent {
//                         label 'opensuse'
//                     }
//                     steps {
//                         cleanWs()
//                         sh('''
//                             sudo zypper install -y git docker
//                             sudo git clone $GITHUB_REPO
//                         ''')
//                     }
//                 }
//                 stage ('Ubuntu Install') {
//                     agent {
//                         label 'ubuntu'
//                     }
//                     steps {
//                         sh('''
//                             sudo apt install -y docker.io
//                         ''')
//                     }
//                 }
//             }
//         }

//         stage('Build Image') {
//             agent {
//                 label 'opensuse'
//             }
//             steps {
//                 withCredentials([sshUserPrivateKey(credentialsId: 'gitlab-ssh', keyFileVariable: 'SSHKEY')]) {
//                     sh('''
//                     set +x
//                     sudo docker build -t "$DOCKERHUB_REPO:${IMAGE_TAG}" -f ./docker-autossh/autossh-with-envs.dockerfile --build-arg SSH_PRV_KEY="$(cat $SSHKEY)" .
//                     ''')
//                 }
//             }
//         }

//         stage('Push Image') {
//             agent {
//                 label 'opensuse'
//             }
//             steps {
//                 withCredentials([usernamePassword(credentialsId: 'docker-user', usernameVariable: 'login', passwordVariable: 'password')]) {
//                     sh('''
//                     sudo docker login -u $login -p $password
//                     sudo docker push $DOCKERHUB_REPO:${IMAGE_TAG}
//                     ''')
//                 }
//             }
//         }

//         stage ('Deploy') {
//             agent {
//                 label 'opensuse'
//             }
//             steps {
//                 withCredentials([sshUserPrivateKey(credentialsId: 'gitlab-ssh', keyFileVariable: 'SSHKEY')]) {
//                     sh '''
//                     ssh -i $SSHKEY -o StrictHostKeyChecking=no root@$REMOTE_HOST "sudo docker pull $DOCKERHUB_REPO:${IMAGE_TAG}"
//                     ssh -i $SSHKEY -o StrictHostKeyChecking=no root@$REMOTE_HOST "sudo docker run -d -e SSH_PORT="${SSH_PORT}" -e SSH_TUNNEL="${SSH_LOCAL_PORT}:${SSH_TUNNEL}:${SSH_REMOTE_PORT}" -e SSH_USER="${SSH_USER}" -e SSH_HOST="${SSH_HOST}" --name autossh --network host --restart unless-stopped $DOCKERHUB_REPO:$IMAGE_TAG"
//                     '''
//                 }
//             }
//             post {
//                 always {
//                     echo "This block always runs after this stage."
//                 }
//             }
//         }
//     }
// }
