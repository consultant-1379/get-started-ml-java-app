{{/* vim: set filetype=mustache: */}}
{{/*
Expand the name of the chart.
*/}}
{{- define "eric-oss-get-started-ml-java-app-init.name" }}
  {{- default .Chart.Name .Values.nameOverride | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
Create chart version as used by the chart label.
*/}}
{{- define "eric-oss-get-started-ml-java-app-init.version" }}
{{- printf "%s" .Chart.Version | replace "+" "_" | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
Expand the name of the chart.
*/}}
{{- define "eric-oss-get-started-ml-java-app-init.fullname" -}}
{{- if .Values.fullnameOverride -}}
  {{- .Values.fullnameOverride | trunc 63 | trimSuffix "-" -}}
{{- else -}}
  {{- $name := default .Chart.Name .Values.nameOverride -}}
  {{- printf "%s" $name | trunc 63 | trimSuffix "-" -}}
{{- end -}}
{{- end -}}

{{/*
Create chart name and version as used by the chart label.
*/}}
{{- define "eric-oss-get-started-ml-java-app-init.chart" }}
{{- printf "%s-%s" .Chart.Name .Chart.Version | replace "+" "_" | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
Create image pull secrets for global (outside of scope)
*/}}
{{- define "eric-oss-get-started-ml-java-app-init.pullSecret.global" -}}
{{- $pullSecret := "" -}}
{{- if .Values.global -}}
  {{- if .Values.global.pullSecret -}}
    {{- $pullSecret = .Values.global.pullSecret -}}
  {{- end -}}
  {{- end -}}
{{- print $pullSecret -}}
{{- end -}}

{{/*
Create image pull secret, service level parameter takes precedence
*/}}
{{- define "eric-oss-get-started-ml-java-app-init.pullSecret" -}}
{{- $pullSecret := (include "eric-oss-get-started-ml-java-app-init.pullSecret.global" . ) -}}
{{- if .Values.imageCredentials -}}
  {{- if .Values.imageCredentials.pullSecret -}}
    {{- $pullSecret = .Values.imageCredentials.pullSecret -}}
  {{- end -}}
{{- end -}}
{{- print $pullSecret -}}
{{- end -}}

{{/*
Generic image image Path (DR-D1121-067)

To use it, you should pass the imagekey, for instance
(include "eric-oss-get-started-ml-java-app-init.imagePath" (set . "ImageKey" "mainImage"))
*/}}
{{- define "eric-oss-get-started-ml-java-app-init.imagePath" }}
    {{- if not .ImageKey }}
        {{- fail "Chart failure: Missing ImageKey on template parameters" -}}
    {{- end }}
    {{- $productInfo := fromYaml (.Files.Get "eric-product-info.yaml") -}}
    {{- $name := (index $productInfo.images .ImageKey).name -}}
    {{- $tag := (index $productInfo.images .ImageKey).tag -}}
    {{- $repoPath := (index $productInfo.images .ImageKey).repoPath -}}
    {{- $registryUrl := (index $productInfo.images .ImageKey).registry -}}

    {{- if ((index .Values.imageCredentials .ImageKey).registry).url }}
        {{- $registryUrl = ((index .Values.imageCredentials .ImageKey).registry).url -}}
    {{- else if (((.Values).imageCredentials).registry).url }}
        {{- $registryUrl = .Values.imageCredentials.registry.url -}}
    {{- else if (((.Values).global).registry).url }}
        {{- $registryUrl = .Values.global.registry.url -}}
    {{- end }}

    {{- if not (kindIs "invalid" (index .Values.imageCredentials .ImageKey).repoPath) }}
        {{- $repoPath = (index .Values.imageCredentials .ImageKey).repoPath -}}
    {{- else if not (kindIs "invalid" (.Values.imageCredentials).repoPath)}}
        {{- $repoPath = .Values.imageCredentials.repoPath -}}
    {{- else if not ((kindIs "invalid" ((.Values.global).registry).repoPath) ) }}
        {{- $repoPath = .Values.global.registry.repoPath -}}
    {{- end }}

    {{- if $registryUrl }}
        {{- $registryUrl = printf "%s/" $registryUrl -}}
    {{- end -}}
    {{- if $repoPath -}}
        {{- $repoPath = printf "%s/" $repoPath -}}
    {{- end -}}
    {{- printf "%s%s%s:%s" $registryUrl $repoPath $name $tag -}}
{{- end -}}

{{/*
Timezone variable
*/}}
{{- define "eric-oss-get-started-ml-java-app-init.timezone" }}
  {{- $timezone := "UTC" }}
  {{- if .Values.global }}
    {{- if .Values.global.timezone }}
      {{- $timezone = .Values.global.timezone }}
    {{- end }}
  {{- end }}
  {{- print $timezone | quote }}
{{- end -}}

{{/*
Create a user defined label (DR-D1121-068, DR-D1121-060)
*/}}
{{ define "eric-oss-get-started-ml-java-app-init.config-labels" }}
  {{- $global := (.Values.global).labels -}}
  {{- $service := .Values.labels -}}
  {{- include "eric-oss-get-started-ml-java-app-init.mergeLabels" (dict "location" .Template.Name "sources" (list $global $service)) }}
{{- end }}

{{/*
Merged labels for Default, which includes Standard and Config
*/}}
{{- define "eric-oss-get-started-ml-java-app-init.labels" -}}
  {{- $standard := include "eric-oss-get-started-ml-java-app-init.standard-labels" . | fromYaml -}}
  {{- $config := include "eric-oss-get-started-ml-java-app-init.config-labels" . | fromYaml -}}
  {{- include "eric-oss-get-started-ml-java-app-init.mergeLabels" (dict "location" .Template.Name "sources" (list $standard $config)) | trim }}
{{- end -}}

{{/*
Return the fsgroup set via global parameter if it's set, otherwise 10000
*/}}
{{- define "eric-oss-get-started-ml-java-app-init.fsGroup.coordinated" -}}
  {{- if .Values.global -}}
    {{- if .Values.global.fsGroup -}}
      {{- if .Values.global.fsGroup.manual -}}
        {{ .Values.global.fsGroup.manual }}
      {{- else -}}
        {{- if eq .Values.global.fsGroup.namespace true -}}
          # The 'default' defined in the Security Policy will be used.
        {{- else -}}
          10000
      {{- end -}}
    {{- end -}}
  {{- else -}}
    10000
  {{- end -}}
  {{- else -}}
    10000
  {{- end -}}
{{- end -}}

{{/*
Create the name of the service account to use
*/}}
{{- define "eric-oss-get-started-ml-java-app-init.serviceAccountName" -}}
  {{- if .Values.serviceAccount.create }}
    {{- default (include "eric-oss-get-started-ml-java-app-init.fullname" .) .Values.serviceAccount.name }}
  {{- else }}
    {{- default "default" .Values.serviceAccount.name }}
  {{- end }}
{{- end }}

{{/*
Create container level annotations
*/}}
{{- define "eric-oss-get-started-ml-java-app-init.container-annotations" }}
    {{- if .Values.appArmorProfile -}}
    {{- $appArmorValue := .Values.appArmorProfile.type -}}
        {{- if .Values.appArmorProfile.type -}}
            {{- if eq .Values.appArmorProfile.type "localhost" -}}
                {{- $appArmorValue = printf "%s/%s" .Values.appArmorProfile.type .Values.appArmorProfile.localhostProfile }}
            {{- end}}
container.apparmor.security.beta.kubernetes.io/eric-oss-get-started-ml-java-app-init: {{ $appArmorValue | quote }}
        {{- end}}
    {{- end}}
{{- end}}

{{/*
Seccomp profile section (DR-1123-128)
*/}}
{{- define "eric-oss-get-started-ml-java-app-init.seccomp-profile" }}
    {{- if .Values.seccompProfile }}
      {{- if .Values.seccompProfile.type }}
          {{- if eq .Values.seccompProfile.type "Localhost" }}
              {{- if .Values.seccompProfile.localhostProfile }}
seccompProfile:
  type: {{ .Values.seccompProfile.type }}
  localhostProfile: {{ .Values.seccompProfile.localhostProfile }}
            {{- end }}
          {{- else }}
seccompProfile:
  type: {{ .Values.seccompProfile.type }}
          {{- end }}
        {{- end }}
      {{- end }}
{{- end }}

{{/*
Annotations for Product Name and Product Number (DR-D1121-064).
*/}}
{{- define "eric-oss-get-started-ml-java-app-init.product-info" }}
ericsson.com/product-name: {{ (fromYaml (.Files.Get "eric-product-info.yaml")).productName | quote }}
ericsson.com/product-number: {{ (fromYaml (.Files.Get "eric-product-info.yaml")).productNumber | quote }}
ericsson.com/product-revision: {{regexReplaceAll "(.*)[+|-].*" .Chart.Version "${1}" | quote }}
{{- end }}

{{/*
Create a user defined annotation (DR-D1121-065, DR-D1121-060)
*/}}
{{ define "eric-oss-get-started-ml-java-app-init.config-annotations" }}
  {{- $global := (.Values.global).annotations -}}
  {{- $service := .Values.annotations -}}
  {{- include "eric-oss-get-started-ml-java-app-init.mergeAnnotations" (dict "location" .Template.Name "sources" (list $global $service)) }}
{{- end }}

Standard labels of Helm and Kubernetes
*/}}
{{- define "eric-oss-get-started-ml-java-app-init.standard-labels" -}}
app.kubernetes.io/name: {{ include "eric-oss-get-started-ml-java-app-init.name" . }}
app.kubernetes.io/managed-by: {{ .Release.Service }}
app.kubernetes.io/instance: {{ .Release.Name }}
app.kubernetes.io/version: {{ include "eric-oss-get-started-ml-java-app-init.version" . }}
helm.sh/chart: {{ include "eric-oss-get-started-ml-java-app-init.chart" . }}
chart: {{ include "eric-oss-get-started-ml-java-app-init.chart" . }}
{{- end -}}

{{/*
Merged annotations for Default, which includes productInfo, prometheus and config
*/}}
{{- define "eric-oss-get-started-ml-java-app-init.annotations" -}}
  {{- $productInfo := include "eric-oss-get-started-ml-java-app-init.product-info" . | fromYaml -}}
  {{- $prometheusAnn := include "eric-oss-get-started-ml-java-app-init.prometheus" . | fromYaml -}}
  {{- $config := include "eric-oss-get-started-ml-java-app-init.config-annotations" . | fromYaml -}}
  {{- include "eric-oss-get-started-ml-java-app-init.mergeAnnotations" (dict "location" .Template.Name "sources" (list $productInfo $prometheusAnn $config)) | trim }}
{{- end -}}

{{/*
Create prometheus info
*/}}
{{- define "eric-oss-get-started-ml-java-app-init.prometheus" -}}
prometheus.io/path: {{ .Values.prometheus.path | quote }}
prometheus.io/port: {{ .Values.service.port | quote }}
prometheus.io/scrape: {{ .Values.prometheus.scrape | quote }}
{{- end -}}

{{/*
Define the role reference for security policy
*/}}
{{- define "eric-oss-get-started-ml-java-app-init.securityPolicy.reference" -}}
  {{- if .Values.global -}}
    {{- if .Values.global.security -}}
      {{- if .Values.global.security.policyReferenceMap -}}
        {{ $mapped := index .Values "global" "security" "policyReferenceMap" "default-restricted-security-policy" }}
        {{- if $mapped -}}
          {{ $mapped }}
        {{- else -}}
          default-restricted-security-policy
        {{- end -}}
      {{- else -}}
        default-restricted-security-policy
      {{- end -}}
    {{- else -}}
      default-restricted-security-policy
    {{- end -}}
  {{- else -}}
    default-restricted-security-policy
  {{- end -}}
{{- end -}}

{{/*
Define the annotations for security policy
*/}}
{{- define "eric-oss-get-started-ml-java-app-init.securityPolicy.annotations" -}}
# Automatically generated annotations for documentation purposes.
{{- end -}}

{{/*
Define Pod Disruption Budget value taking into account its type (int or string)
*/}}
{{- define "eric-oss-get-started-ml-java-app-init.pod-disruption-budget" -}}
  {{- if kindIs "string" .Values.podDisruptionBudget.minAvailable -}}
    {{- print .Values.podDisruptionBudget.minAvailable | quote -}}
  {{- else -}}
    {{- print .Values.podDisruptionBudget.minAvailable | atoi -}}
  {{- end -}}
{{- end -}}

{{/*
Define tolerations to comply to DR-D1120-060
*/}}
{{- define "eric-oss-get-started-ml-java-app-init.tolerations" -}}
{{- if .Values.tolerations -}}
  {{- toYaml .Values.tolerations -}}
{{- end -}}
{{- end -}}

{{/*
Create a merged set of nodeSelectors from global and service level.
*/}}
{{- define "eric-oss-get-started-ml-java-app-init.nodeSelector" -}}
{{- $globalValue := (dict) -}}
{{- if .Values.global -}}
    {{- if .Values.global.nodeSelector -}}
      {{- $globalValue = .Values.global.nodeSelector -}}
    {{- end -}}
{{- end -}}
{{- if .Values.nodeSelector -}}
  {{- range $key, $localValue := .Values.nodeSelector -}}
    {{- if hasKey $globalValue $key -}}
         {{- $Value := index $globalValue $key -}}
         {{- if ne $Value $localValue -}}
           {{- printf "nodeSelector \"%s\" is specified in both global (%s: %s) and service level (%s: %s) with differing values which is not allowed." $key $key $globalValue $key $localValue | fail -}}
         {{- end -}}
     {{- end -}}
    {{- end -}}
    nodeSelector: {{- toYaml (merge $globalValue .Values.nodeSelector) | trim | nindent 2 -}}
{{- else -}}
  {{- if not ( empty $globalValue ) -}}
    nodeSelector: {{- toYaml $globalValue | trim | nindent 2 -}}
  {{- end -}}
{{- end -}}
{{- end -}}

{{/*
    Define Image Pull Policy
*/}}
{{- define "eric-oss-get-started-ml-java-app-init.registryImagePullPolicy" -}}
    {{- $globalRegistryPullPolicy := "IfNotPresent" -}}
    {{- if .Values.global -}}
        {{- if .Values.global.registry -}}
            {{- if .Values.global.registry.imagePullPolicy -}}
                {{- $globalRegistryPullPolicy = .Values.global.registry.imagePullPolicy -}}
            {{- end -}}
        {{- end -}}
    {{- end -}}
    {{- print $globalRegistryPullPolicy -}}
{{- end -}}

{{/*
Define the log streaming method parameter (DR-470222-010)
*/}}
{{- define "eric-oss-get-started-ml-java-app-init.streamingMethod" -}}
{{- $streamingMethod := "direct" -}}
{{- if .Values.global -}}
  {{- if .Values.global.log -}}
      {{- if .Values.global.log.streamingMethod -}}
        {{- $streamingMethod = .Values.global.log.streamingMethod }}
      {{- end -}}
  {{- end -}}
{{- end -}}
{{- if .Values.log -}}
  {{- if .Values.log.streamingMethod -}}
    {{- $streamingMethod = .Values.log.streamingMethod }}
  {{- end -}}
{{- end -}}
{{- print $streamingMethod -}}
{{- end -}}

{{/*
Define the label needed for reaching eric-log-transformer (DR-470222-010)
*/}}
{{- define "eric-oss-get-started-ml-java-app-init.directStreamingLabel" -}}
{{- $streamingMethod := (include "eric-oss-get-started-ml-java-app-init.streamingMethod" .) -}}
{{- if or (eq "direct" $streamingMethod) (eq "dual" $streamingMethod) }}
logger-communication-type: "direct"
{{- end -}}
{{- end -}}

{{/*
Define logging environment variables and decide on expected behavior (DR-470222-010)
*/}}
{{ define "eric-oss-get-started-ml-java-app-init.loggingEnv" }}
{{- $streamingMethod := (include "eric-oss-get-started-ml-java-app-init.streamingMethod" .) -}}
{{- if or (eq "direct" $streamingMethod) (eq "dual" $streamingMethod) -}}
  {{- if eq "direct" $streamingMethod }}
- name: LOGBACK_CONFIG_FILE
  value: "classpath:logback-http.xml"
  {{- end }}
  {{- if eq "dual" $streamingMethod }}
- name: LOGBACK_CONFIG_FILE
  value: "classpath:logback-dual.xml"
  {{- end }}
- name: LOGSTASH_DESTINATION
  value: eric-log-transformer
- name: LOGSTASH_PORT
  value: "9080"
- name: POD_NAME
  valueFrom:
    fieldRef:
      fieldPath: metadata.name
- name: POD_UID
  valueFrom:
    fieldRef:
      fieldPath: metadata.uid
- name: CONTAINER_NAME
  value: eric-oss-get-started-ml-java-app-init
- name: NODE_NAME
  valueFrom:
    fieldRef:
      fieldPath: spec.nodeName
- name: NAMESPACE
  valueFrom:
    fieldRef:
      fieldPath: metadata.namespace
{{- else if eq $streamingMethod "indirect" }}
- name: LOGBACK_CONFIG_FILE
  value: "classpath:logback-json.xml"
{{- else }}
  {{- fail ".log.streamingMethod unknown" }}
{{- end -}}
{{ end }}

{{/*
DR-D470217-007-AD This helper defines whether this service enter the Service Mesh or not.
*/}}
{{- define "eric-oss-get-started-ml-java-app-init.service-mesh-enabled" }}
  {{- $globalMeshEnabled := "false" -}}
  {{- if .Values.global -}}
    {{- if .Values.global.serviceMesh -}}
        {{- $globalMeshEnabled = .Values.global.serviceMesh.enabled -}}
    {{- end -}}
  {{- end -}}
  {{- $globalMeshEnabled -}}
{{- end -}}

{{/*
Check global.security.tls.enabled
*/}}
{{- define "eric-oss-get-started-ml-java-app-init.global-security-tls-enabled" -}}
{{- if  .Values.global -}}
  {{- if  .Values.global.security -}}
    {{- if  .Values.global.security.tls -}}
      {{- .Values.global.security.tls.enabled | toString -}}
    {{- else -}}
      {{- "false" -}}
    {{- end -}}
  {{- else -}}
    {{- "false" -}}
  {{- end -}}
{{- else -}}
  {{- "false" -}}
{{- end -}}
{{- end -}}
