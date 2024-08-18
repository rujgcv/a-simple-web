<template>
  <div style="margin-top: 10px">
    <el-card class="card-one" shadow="hover" v-show="!v"></el-card>
    <el-card class="card-one" shadow="hover" v-show="v">
      <el-image style="width: 120px; height: 160px" :src="data.imgUrl+'?t='+new Date().getTime()" fit="cover" />
      <div class="card-one-context">
        <div class="title">
            <span>{{ data.name }}</span>
            <div style="display: inline-block;float: right;padding-right: 20px;">
                <span style="color:#ff9900">{{ data.grade }}</span>
            </div>
        </div>
        <div style="margin-top: 5px;">
            <div class="writer" >{{ data.writer }}</div>
        </div>
        
        <div class="context">
          {{ data.description }}
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
  import { ref,toRefs, defineProps, reactive, onBeforeMount, onMounted,watch } from "vue";
  import axios from "axios";

  const props = defineProps({
    bookId: Number,
  });
  const {bookId} = toRefs(props);

  let data = reactive({
    id: bookId.value,
    name : '',
    writer : '',
    imgUrl : '',
    description : '',
    grade : 0.00
  })

  const v = ref(false)

  const load = () => {
    axios.get("/book/getBook", {params : {id : bookId.value}}).then(response => {
      data = response.data
      // console.log(data);
      data.grade = data.grade.toFixed(2)
      v.value = true
    })
  }
  
  watch(()=>bookId.value,(newValue) => {
    load();
  })

</script>

<style scoped>
.card-one {
  width: 450px;
  height: 200px;
  background-color: #fff;
  position: relative;
  cursor: pointer;
}

.card-one-context {
  width: 270px;
  height: 160px;
  display: inline-block;
  position: absolute;
  left: 160px;
}
.title {
  color: #26b0f5; 
  font-weight: 600
}
.title:after {
  content: '';
  display: block;
  clear: both;
}
.context {
  width: 270px;
  height: 102px;
  margin-top: 8px;
  font-size: 13px;
  color: #5b5b5b;
  overflow: hidden;
  text-overflow: ellipsis;
}
.writer{
  height: 20px;
  line-height: 20px;
  font-size: 14px;
  border: #26b0f5 1px solid;
  display: inline-block;
  padding: 0 3px;
  border-radius: 3px;
  color: #26b0f5;
  background-color: #fff;
}
</style>
