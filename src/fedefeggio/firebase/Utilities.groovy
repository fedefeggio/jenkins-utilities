/* groovylint-disable */
package fedefeggio.firebase;

class Utilities implements Serializable {

    def steps
    Utilities(steps) { this.steps = steps }

    def deployHosting(String firebaseToken) {
        steps.withEnv(['FIREBASE_TOKEN=' + firebaseToken]) {
            steps.sh '''
                firebase deploy --only hosting --token ${FIREBASE_TOKEN}
            '''
        }
    }
}
