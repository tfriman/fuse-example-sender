# Deployment to OpenShift

```bash
mvn clean -DskipTests fabric8:deploy -Popenshift
```

## Undeployment

```bash
mvn -DskipTests fabric8:undeploy -Popenshift
```

## Calling when deployed on OpenShift

```bash
curl $(oc get routes fuse-sender -n project_name --template="{{ .spec.host }}")/camel/message/foo
```


