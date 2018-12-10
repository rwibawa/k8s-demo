# Setting up the Environment

To play with these examples, you can install locally Kubernetes & Docker using `[Minikube](https://kubernetes.io/docs/getting-started-guides/minikube/)` within a Virtual Machine
managed by a hypervisor (Xhyve, Virtualbox or KVM) if your machine is not a native Unix operating system.


When the minikube  is installed on your machine, you can start kubernetes using this command:
```
minikube start
```

You also probably want to configure your docker client to point the minikube docker deamon with:
```
eval $(minikube docker-env)
```

This will make sure that the docker images that you build are available to the minikube environment.

# Hello World Example

This Spring Boot application exposes an endpoint that we can call to receive a `Hello World` message as response. The application is configured using the
@RestController and the method returning the message is annotated with `@RequestMapping("/")`

The uberjar of the Spring Boot application is packaged within a Docker image using the [Fabric8 Maven plugin](maven.fabric8.io) and next deployed top of the Kubernetes management platform as a pod
using the replication controller created by the plugin.


Once you have the environment set up (minikube or kubectl configured against a kubernetes cluster)

You can play with this Spring Boot application in the cloud using the following maven command to deploy it:
```
$ ./mvnw clean package fabric8:deploy -Pkubernetes -Dmaven.test.skip=true -Dmaven.repo.local=./.m2/repository -s ./settings.xml
```

**Note**: Unfortunately, when you deploy using the fabric8 plugin, the readyness and liveness probes fail to point to the right actuator URL due a lack of support for spring boot.
This push you to edit the generated deployment inside kubernetes and change these probes which points to "path": "/health" to  "path": "/actuator/health".
This will make your deployment go green. This issue is already reported into the fabric8 community: https://github.com/fabric8io/fabric8-maven-plugin/issues/1178

When the application has been deployed, you can access its service or endpoint url using this command:
```
minikube service kubernetes-hello-world --url
```

And next you can curl the endpoint using the url returned by the previous command

```
curl http://IP_OR_HOSTNAME/
```

then

```
curl http://IP_OR_HOSTNAME/services
```

Should return you the list of available services discovered by the DiscoveryClient

```
mvn clean install -Pintegration
```

### Known Issues
#### [User "system:serviceaccount:default:default" cannot get at the cluster scope](https://github.com/fabric8io/fabric8/issues/6840)
You should bind service account `system:serviceaccount:default:default` (which is the default account bound to Pod) with role `cluster-admin`, just create a yaml (named like `fabric8-rbac.yaml`) with following contents:
```yaml
# NOTE: The service account `default:default` already exists in k8s cluster.
# You can create a new account following like this:
#---
#apiVersion: v1
#kind: ServiceAccount
#metadata:
#  name: <new-account-name>
#  namespace: <namespace>

---
apiVersion: rbac.authorization.k8s.io/v1beta1
kind: ClusterRoleBinding
metadata:
  name: fabric8-rbac
subjects:
  - kind: ServiceAccount
    # Reference to upper's `metadata.name`
    name: default
    # Reference to upper's `metadata.namespace`
    namespace: default
roleRef:
  kind: ClusterRole
  name: cluster-admin
  apiGroup: rbac.authorization.k8s.io
```

Apply it:
```bash
$ kubectl apply -f fabric8-rbac.yaml
```

Delete the service account:
```bash
$ kubectl delete -f fabric8-rbac.yaml
```
