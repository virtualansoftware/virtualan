pipeline {
    agent any
    
    tools {
        maven "Maven"
    }
    
    stages {
        stage ('Compile Stage') {
            steps {
                  bat 'mvn clean compile'
            }
        }
        stage ('Build Stage') {
            steps {
                
                    bat 'mvn clean install'
            }
        }
       stage ('Test Sample Stage') {
            steps {
                
                    bat 'mvn clean install -Denv=samples '
            }
        }
        stage('Generate HTML report') {
           steps { 
              cucumber buildStatus: 'SUCCESS',
                reportTitle: 'My report',
                fileIncludePattern: '**/*.json',
                trendsLimit: 10,
                classifications: [
                    [
                        'key': 'Browser',
                        'value': 'Firefox'
                    ]
                ]
           }
       }
        
       
    }
   
     post {
            always {
                cucumber '**/cucumber.json'
            }
         }   
}
