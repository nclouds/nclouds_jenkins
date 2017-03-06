freeStyleJob('clean-old-images') {
  triggers {
    cron('H 0 * * *')
  }
  steps {
    shell('''\
      #!/bin/bash
      set -xe
      {
        docker images | grep '7 days ago' | awk '{print $1":"$2}' | xargs --no-run-if-empty docker rmi
        docker images | grep '7 days ago' | awk '{print $3}' | xargs --no-run-if-empty docker rmi
      } || echo "errors are expected the images could be in use or untagged"
          '''.stripIndent())
  }
}
