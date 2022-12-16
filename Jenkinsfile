pipeline { 
    agent {
        docker {
            image 'nginx:latest'
        }
    }
    stages {
        stage {
            steps {
                sh 'curl http://localhost'
            }
        }
    }
}
