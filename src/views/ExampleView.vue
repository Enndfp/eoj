<template>
  <div id="exampleView">
    <CodeEditor :value="codeValue" :handle-change="onCodeChange"></CodeEditor>
    <MdEditor :value="mdValue" :handle-change="onMdChange"></MdEditor>
  </div>
</template>

<script lang="ts" setup>
import { ref } from "vue";
import MdEditor from "@/components/MdEditor.vue";
import CodeEditor from "@/components/CodeEditor.vue";

const mdValue = ref("");
const onMdChange = (v: string) => {
  mdValue.value = v;
  console.log(v);
};

const codeValue = ref("");
const onCodeChange = (v: string) => {
  codeValue.value = v;
  console.log(v);
};
</script>
