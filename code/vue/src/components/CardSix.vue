<template>
  <div v-show="v">
    <div class="box-card">
      <div class="header">
        <div class="vertical"></div>
        <span style="position: absolute; left: 10px;top: 0;line-height: 20px;">热门小说</span>
      </div>
      <div class="divider"></div>
      <div>
        <div class="tag" v-for="data in dataList" :key="user">
          <div class="tag-one">{{ data.type }}</div>
          <div class="tag-two" @click="intoBook(data.id)">{{ data.name }}</div>
          <div class="tag-three">{{ data.writer }}</div>
        </div>
      </div>
    </div>
  </div>
  
</template>

<script setup>
  import { ref,toRefs, defineProps, reactive, onBeforeMount, onMounted,watch } from "vue";
  import axios from "axios";

  const {bookList} = ref();

  let dataList = reactive([
    {
      id: 0,
      name : '',
      writer : '',
      type: '',
      grade : 0.00
    }
  ])

  const v = ref(false)

  const load = () => {
    axios.get("/book/getPopular").then(response => {
      dataList = response.data
      v.value = true
    })
  }

  onBeforeMount(() => {
    load();
  })

  function intoBook(id){
    window.open('../book?id='+id);
  }

  </script>


<style scoped>
.box-card {
  padding: 10px 10px 0 10px;
  width: 260px;
  background-color: #fff;
  border: 1px solid #e4e7ed;
  border-radius: 4px;
}
.divider {
  height: 1px;
  margin-top: 10px;
  width: 258px;
  background-color: #dcdfe6;
}
.vertical {
  margin-top: 2px;
  width: 2.8px;
  height: 20px;
  background-color: #26b0f5;
}
.header {
  position: relative;
}
.tag {
  padding: 8px 0;
  border-bottom: 1px solid #e4e7ed;
  font-size: 12px; 
}
.tag::after{
  content: '';
  display: block;
  clear: both;
}
.tag:last-child {
  border: none;
}
.tag-one {
  float: left;
  color: #b2b2b2;
}
.tag-two {
  width: 146px;
  height: 16px;
  float: left;
  margin-left: 5px;
  color: #1497d8;
}
.tag-two:hover {
  color: #fc6b0a;
  cursor: pointer;
  text-decoration-line: underline;
}
.tag-three {
  float: right;
  color: #696969;
}
</style>
