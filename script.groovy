#!/usr/bin/env groovy
package jenkins

kubernetes_label = 'kubernetes_ah_stage3'
kubernetes_highload_label = 'kubernetes_ah_highload'
build_label = 'ah_build_gic'

def lib
node {
    checkout([$class: 'GitSCM',
              branches: [[name: "${params.BRANCH}"]], doGenerateSubmoduleConfigurations: false, extensions: [[$class: 'WipeWorkspace']], submoduleCfg: [], 
              userRemoteConfigs: [[
                  credentialsId: 'gerrit-id',
                  refspec: params.GERRIT_REFSPEC + ':' + params.GERRIT_REFSPEC,
                  url: 'https://gerrit.ericsson.se/a/adp-gs/adp-gs-ah']]])

    //git credentialsId: 'gerrit-id', url: 'https://gerrit.ericsson.se/a/adp-gs/adp-gs-ah'
    /*checkout([$class: 'GitSCM',
              branches: [[name: "${params.BRANCH}"]],
              doGenerateSubmoduleConfigurations: false,
              extensions: [[$class: 'CleanCheckout']], 
              submoduleCfg: [],
              userRemoteConfigs: [[credentialsId: 'gerrit-id',
                refspec: params.GERRIT_REFSPEC + ':' + params.GERRIT_REFSPEC,
                url: 'https://gerrit.ericsson.se/a/adp-gs/adp-gs-ah']]
            ])
    */
    lib = load("test/src/main/groovy/jenkins/jenkinsLib.groovy")
}

def custom_checkout() {
    //branch='*/master'
    //branch='refs/changes/60/5515760/2'
    checkout([$class: 'GitSCM',
              branches: [[name: "${params.BRANCH}"]],
              doGenerateSubmoduleConfigurations: false,
              extensions: [],
              submoduleCfg: [],
              userRemoteConfigs: [[credentialsId: 'gerrit-id',
                refspec: params.GERRIT_REFSPEC + ':' + params.GERRIT_REFSPEC,
                url: 'https://gerrit.ericsson.se/a/adp-gs/adp-gs-ah']]
            ])
}

pipeline {
    agent any
    parameters{
        booleanParam(name: 'SKIP_TESTS', defaultValue: false, description: 'skip tests?')
        //in a choice parameter, the first item is the default one
        choice(name: 'IMAGE_DELIVERY',
            choices: 'conditional\nforce\ndisable',
            description: 'policy for delivering an image and triggering the next stage: when a new image is available (conditional), always (force) or never (disable)'
        )
        string(name: 'STAB_DURATION', defaultValue: '10', description: 'The duration of the stability test (in floating point hours)')
        string(name: 'HIGHLOAD_DURATION', defaultValue: '2', description: 'The duration of the high load test (in floating point hours)')
        string(name: 'GERRIT_REFSPEC', defaultValue: '', description: 'used when running tests on unmerged changes, when set it disables image delivery')
        string(name: 'BRANCH', defaultValue: '*/master', description: '')
    }
    options {
        disableConcurrentBuilds()
        timeout(activity: true, time: 2, unit: 'HOURS')
        buildDiscarder(logRotator(numToKeepStr: '20'))
    }
    environment {
        MS_NAME = "eric-fh-alarm-handler"
        IMAGE_DELIVERY = "${params.IMAGE_DELIVERY}"
    }
    stages {
        stage('Setup') {
            steps {
                //custom_checkout()
                echo "Setup"
                //sh "printenv | sort"
                sh """
                    ls
                    git show | head
                """
            }
        }
        stage('Cleanup') {
            agent {
                label "${build_label}"
            }
            steps {
                //custom_checkout()
                echo "Cleanup"
                //sh "printenv | sort"
                sh """
                    ls
                    git show | head
                """
            }
        }
        stage('Final') {
            steps {
                checkout scm
                //custom_checkout()
                echo "Final"
                //sh "printenv | sort"
                sh """
                    ls
                    git show | head
                """
            }
        }
    }
}
