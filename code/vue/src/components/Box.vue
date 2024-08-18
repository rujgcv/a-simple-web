<template>
    <div class="all" v-show="!v">
        <el-skeleton :rows="50" />
    </div>
    <div class="all" v-show="v">
        <div class="main">
            <div v-show="rank">                
                <el-select v-model="valueType" class="m-2" placeholder="请输入类别" style="width: 100px;margin-right: 10px;" @change="load()">
                    <el-option
                    v-for="item in optionsType"
                    :key="item.value"
                    :label="item.label"
                    :value="item.value"
                    />
                </el-select>
                <el-select v-model="valueDate" class="select" placeholder="Select" @change="load()">
                    <el-option
                    v-for="item in optionsDate"
                    :key="item.value"
                    :label="item.label"
                    :value="item.value"
                    />
                </el-select>
                <div class="f">每日0点更新榜单</div>
            </div>
            <div style="margin-top: 10px" v-for="data in dataList" :key="data.id">
                <div class="card-one">
                    <el-image style="width: 110px; height: 145px" class="img" :src="data.imgUrl+'?t='+new Date().getTime()" fit="cover" @click="intoBook(data.id)"/>
                    <div class="card-one-context">
                    <div class="title">
                        <b class="name" @click="intoBook(data.id)">《{{data.name}}》</b><el-divider direction="vertical" /><span style="color: #696969;">{{ data.writer }}</span>
                        <div style="display: inline-block;float: right;padding-right: 20px;">
                            <span style="color:#ff9900">{{ data.grade.toFixed(2) }}</span>
                        </div>
                    </div>
                    
                    <div class="context">
                        <b style="color: #000;">小说简介: </b>
                        {{ data.description }}
                    </div>

                    <span class="detail">
                        <b>发布时间: {{ data.createTime }}</b>
                    </span>
                    </div>
                </div>
            </div>

            
            <div class="demo-pagination-block">
                <el-pagination
                v-model:current-page="pageNum"
                :page-size="pageSize"
                layout="total, prev, pager, next"
                :total="total"
                @size-change="handleSizeChange"
                @current-change="handleCurrentChange"
                />
            </div>
        </div>

        <div class="aside">
            <div class="ccc" :style="'height:'+cch+'px;'">
                <el-affix target=".ccc" :offset="100">
                    <CardSix class="cardTwo"></CardSix>
                </el-affix>
            </div>
        </div>
    </div>
</template>

<script setup lang="ts">
    import { onBeforeMount, ref, toRefs,reactive,watch } from 'vue'
    import { createApp } from 'vue';
    import CardSix from './CardSix.vue'
    import axios from 'axios';

    const props = defineProps({
        bookType: String,
    })
    const {bookType} = toRefs(props)

    const cch = ref()

    const optionsDate = [
        {
            value: 'day',
            label: '日排行榜',
        },
        {
            value: 'month',
            label: '月排行榜',
        },
        {
            value: 'all',
            label: '总排行榜',
        }
    ]
    const valueDate = ref(optionsDate[0].value)

      // 标签下拉选择
    const valueType = ref('全部')

    const optionsType = [
        {
            value: '全部',
            label: '全部',
        },
        {
            value: '穿越',
            label: '穿越',
        },
        {
            value: '重生',
            label: '重生',
        },
        {
            value: '修仙',
            label: '修仙',
        },
        {
            value: '玄幻',
            label: '玄幻',
        },
        {
            value: '耽美',
            label: '耽美',
        },
        {
            value: '言情',
            label: '言情',
        }
    ]

    const pageNum = ref(1)
    const pageSize = ref(10)
    const total = ref(100)
    const handleSizeChange = (val: number) => {
        pageSize.value = val;
        load()
    };
    const handleCurrentChange = (val: number) => {
        pageNum.value = val;
        console.log(pageNum.value);
        v.value = false
        load();
    }

    let dataList = reactive([
        {
            id: 0,
            name : '',
            imgUrl : '',
            writer : '',
            type : '',
            description : '',
            createTime : '',
            grade : 0.00
        }
    ])

    const v = ref(false)
    const rank = ref(false)

    function loadSuccess(){
        cch.value = dataList.length * 198
        v.value = true
    }

    const load = () => {
        if(bookType!.value === '排行榜'){
            v.value = false
            rank.value = true
            axios.get("/book/rank", {
                params : {
                    pageNum: pageNum.value,
                    pageSize: pageSize.value,
                    type: valueType.value,
                    date: valueDate.value
                }
            }).then(response => {
                console.log(response);
                dataList = response.data.data
                total.value = response.data.total
                setTimeout(()=>{
                    loadSuccess();
                },200)
            })
        }else if(bookType!.value === '最新发布'){
            console.log(pageNum.value);
            axios.get("/book/page", {
                params : {
                    pageNum: pageNum.value,
                    pageSize: pageSize.value,
                    name: '',
                    writer: '',
                    type: '',
                    order : 'desc'
                }
            }).then(response => {
                console.log(response);
                dataList = response.data.data
                total.value = response.data.total
                setTimeout(()=>{
                    loadSuccess();
                },200)
            })
        }else {
            axios.get("/book/page", {
                params : {
                    pageNum: pageNum.value,
                    pageSize: pageSize.value,
                    name: '',
                    writer: '',
                    type: bookType!.value,
                    order : 'desc'
                }
            }).then(response => {
                console.log(response);
                dataList = response.data.data
                total.value = response.data.total
                setTimeout(()=>{
                    loadSuccess();
                },200)
            })
        }
        window.scrollTo({
            top: 0, //回到顶部
            left: 0,
            behavior: "smooth",
        });
    }



    onBeforeMount(()=>{
        load();
    })

    function intoBook(id){
        window.open('../book?id='+id);
    }

</script>

<style scoped>
    .aside {
        /* height: 1956px; */
        width: 280px;
        float: left;
        margin: 10px 0 0 20px;
    }
    .ccc{
        /* height: 1956px; */
        width: 280px;
    }
    .asid:after{
        content: '';
        display: block;
        clear: both;
    }
    .cardTwo {
        margin-top: 20px;
    }
    .cardTwo:first-child {
        margin: 0;
    }
    .main {
        /* width: 924px; */
        float: left;
        position: relative;
    }
    .main:after{
        content: '';
        display: block;
        clear: both;
    }   
    .all::after{
        content: '';
        display: block;
        clear: both
    }
    .all {
        margin-bottom: 20px;
    }
    .demo-pagination-block {
        height: 40px;
        margin-top: 10px;
        line-height: 40px;
        padding-left: 250px;
        background-color: #fff;
        border: 1px solid #e4e7ed;
        border-radius: 4px;
    }

    .card-one {
    width: 882px;
    height: 144px;
    background-color: #fff;
    position: relative;
    border: 1px solid #e4e7ed;
    border-radius: 4px;
    padding: 20px;
}
  .card-one-context {
    width: 750px;
    height: 144px;
    display: inline-block;
    position: absolute;
    left: 150px;
  }
  .title {
    color: #000; 
  }
  .title:after {
    content: '';
    display: block;
    clear: both;
  }
  .context {
    width: 750px;
    height: 84px;
    margin: 20px 0 2px 0;
    font-size: 13px;
    color: #5b5b5b;
    overflow: hidden;
    text-overflow: ellipsis;
  }
  .detail{
    font-size: 12px;
    color: rgb(255, 111, 0)
  }
  .name:hover {
    color: #fc6b0a;
    cursor: pointer;
    text-decoration-line: underline;
  }
  .f{
    color: #696969;
    /* display: inline-block; */
    height: 32px;
    font-size: 16px;
    line-height: 32px;
    position: absolute;
    top: 0px;
    right: 0px;
  }

  .img{
    cursor: pointer;
  }
</style>