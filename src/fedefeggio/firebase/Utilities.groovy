/* groovylint-disable */
package fedefeggio.firebase;

class Utilities implements Serializable {

    def steps
    Utilities(steps) { this.steps = steps }

    def deployHosting(String firebaseToken, String projectName) {
        steps.withEnv(['FIREBASE_TOKEN=' + firebaseToken, 'PROJECT_NAME=' + projectName]) {
            steps.sh '''
                firebase deploy --only hosting --project ${PROJECT_NAME} --token ${FIREBASE_TOKEN}
            '''
        }
    }
}
