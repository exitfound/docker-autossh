pipeline {
    agent {
        label 'agent-1'
    }
    // environment {
    //     GITHUB_REPO = 'https://github.com/exitfound/docker-autossh'
    // }

    parameters {
        string(name: 'GITHUB_REPO', defaultValue: 'https://github.com/exitfound/docker-autossh', trim: true, description: 'Тэг для образа autossh:')
    }

    }
    stages { 
           stage ('Test') { 
                steps { 
                    cleanWs()
                    sh('''
                        curl $GITHUB_REPO
                    ''')
               }
           }
       }
}
