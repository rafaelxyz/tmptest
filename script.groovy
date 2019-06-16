#!/usr/bin/env groovy
package jenkins

kubernetes_label = 'kubernetes_ah_stage3'
kubernetes_highload_label = 'kubernetes_ah_highload'
build_label = 'ah_build_gic'

def lib

node {
    git branch: "${BRANCH}"
    //checkout([$class: 'GitSCM',
    //          branches: [[name: "${params.BRANCH}"]],
    //          doGenerateSubmoduleConfigurations: false,
    //          extensions: [],
    //          submoduleCfg: [],
    //          userRemoteConfigs: [[credentialsId: 'gerrit-id',
    //            //refspec: params.GERRIT_REFSPEC + ':' + params.GERRIT_REFSPEC,
    //            //url: 'https://gerrit.ericsson.se/a/adp-gs/adp-gs-ah'
    //            ]]
    //        ])
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
        string(name: 'GERRIT_REFSPEC', defaultValue: '', description: 'used when running tests on unmerged changes, when set it disables image delivery')
        string(name: 'BRANCH', defaultValue: 'origin/master', description: '')
    }
    options {
        disableConcurrentBuilds()
        timeout(activity: true, time: 2, unit: 'HOURS')
        buildDiscarder(logRotator(numToKeepStr: '20'))
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
