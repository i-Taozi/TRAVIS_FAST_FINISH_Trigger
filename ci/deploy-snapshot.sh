if [ "$TRAVIS_REPO_SLUG" == "intendia-oss/rxgwt" ] && \
   [ "$TRAVIS_JDK_VERSION" == "openjdk11" ] && \
   [ "$TRAVIS_PULL_REQUEST" == "false" ] && \
   [ "$TRAVIS_BRANCH" == "2.x" ]; then

  mvn -s ci/settings.xml clean source:jar deploy -Dmaven.test.skip=true -Dinvoker.skip=true
fi
