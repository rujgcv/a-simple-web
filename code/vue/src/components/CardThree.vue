<template>
    <div v-show="v">
      <div class="box-card">
        <div class="header">
          <div class="vertical"></div>
          <span style="position: absolute; left: 10px;top: 0;line-height: 20px;">{{ type }}</span>
        </div>

        <div class="divider"></div>

        <div class="firstbook">
            <div style="position: absolute">
                <el-image style="width: 75px; height: 100px" class="img" @click="intoBook(dataList[0].id)" :src="dataList[0].imgUrl+'?t='+new Date().getTime()" fit="cover" />
            </div>

            <div class="card-one-context">
                <div class="title" @click="intoBook(dataList[0].id)">
                    {{dataList[0].name}}
                </div>
                <div style="margin-top: 10px;font-size: 14px;">
                    <div style="width: 160px;height: 20px;">作者: {{ dataList[0].writer }}</div>
                    <div style="margin-top: 10px;">
                        评分: 
                        <span style="color:#ff9900">{{ dataList[0].grade.toFixed(2) }}</span>
                    </div>
                </div>
            </div>
        </div>

        <ul>
          <li class="tag" v-for="data in dataList1" :key="user">
            <span class="tag-one" @click="intoBook(data.id)">{{ data.name }}</span>
            <span style="color: #6a6a6a;"> / </span>
            <span class="tag-two">{{ data.writer }}</span>
          </li>
        </ul>
      </div>
    </div>
    
  </template>
  
  <script setup>
  import { ref,toRefs, defineProps, reactive, onBeforeMount, onMounted,watch } from "vue";
  import axios from "axios";

  const props = defineProps({
    bookList: Array,
  });
  const {bookList} = toRefs(props);
  const type = ref()
  let idList = reactive([])

  let dataList = reactive([
    {
      id: 0,
      name : '',
      writer : '',
      imgUrl : '',
      description : '',
      grade : 0.00
    }
  ])

  let dataList1 = reactive([
  ])

  const v = ref(false)

  const load = () => {
    axios.post("/book/getBookList", idList).then(response => {
      dataList = response.data
      for(var i=1;i<dataList.length;i++){
        dataList1.push(dataList[i])
      }
      v.value = true
    })
  }
  
  watch(()=>bookList.value,(newValue) => {
    type.value = bookList.value.type
    idList = bookList.value.idList
    load();
  })

  function intoBook(id){
    window.open('../book?id='+id);
  }
  </script>
  
  
  <style scoped>
  .box-card {
    padding: 10px 10px 0 10px;
    width: 270px;
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
    /* border-bottom: 1px solid #e4e7ed; */
    font-size: 12px; 
  }
  .tag:last-child {
    border: none;
  }
  .tag-one {
    color: #1497d8;
  }
  .tag-one:hover {
    color: #fc6b0a;
    cursor: pointer;
    text-decoration-line: underline;
  }
  .tag-two {
    color: #696969;
  }
  .firstbook {
    margin-top: 8px;
    height: 100px;
  }
  .card-one-context {
  width: 170px;
  height: 100px;
  display: inline-block;
  position: absolute;
  left: 100px;
}
.title {
    width: 160px;
    height: 20px;
  color: #26b0f5; 
  font-weight: 600;
}
.title:hover {
  color: #fc6b0a;
  cursor: pointer;
  /* text-decoration-line: underline; */
}
.title:after {
  content: '';
  display: block;
  clear: both;
}

.img:hover{
  cursor: pointer;
}
  </style>
  