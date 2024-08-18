<template>
    <div class="all" v-show="!v">
        <el-skeleton :rows="50" />
    </div>
    <div class="all" v-show="v">
        <div class="header">
            <h2 style="display: inline;">评论 </h2><span style="color:#696969">{{ total }}</span>
            <div style="display: inline;margin-left: 40px;">
                <!-- <span :style="'color:' + (selectNew?'#000':'#696969')" class="order">最热</span>
                <el-divider direction="vertical" />
                <span :style="'color:' + ((!selectNew)?'#000':'#696969')" class="order">最新</span> -->
            </div>
        </div>
        <div class="myView">
            <div class="demo-basic--circle" style="float: left;">
                <div style="margin: 15px 30px 0 15px;">
                    <el-avatar :size="50" :src="userInfo.avatarUrl+'?t='+new Date().getTime()" />
                </div>
            </div>
            <div class="write">
                <el-input type="textarea" :rows="3" placeholder="输入评论" style="width: 600px;" v-model="myView" maxlength="1000"/>
            </div>
            <div style="float: left;margin-left: 420px;margin-top: 14px;">
                <el-button type="primary" @click="myViewSend()">发表</el-button>
            </div>
        </div>
        <div class="allReview">
            <div v-if="reviews.length === 0" style="width: 100%;height: 300px;">
                <el-empty description="暂无评论" />
            </div>
            <div class="reviewRoot" v-for="(review,index) in reviews" :key="index">
                <el-divider />
                <div style="height: 60px;width: 80px;margin-left: 20px;">
                    <div style="margin: 15px 30px 0 15px;">
                        <el-avatar :size="50" :src="review.avatarUrl+'?t='+new Date().getTime()" />
                    </div>
                </div>
                <div class="reviewRootName">{{ review.username }}</div>
                <div class="reviewRootContent">
                    {{ review.content }}
                    <div class="information">
                        <div style="float: left;">{{ review.createTime }}</div>
                        <div class="huifu" @click="reply1(review,index)">回复</div>
                        <!-- <div style="margin-right: 15px;float: right;">
                            <img src="../../assets/img/like.png" class="like">
                        </div>   -->
                    </div>
                    <div class="childrenReview">
                        <div class="creview" v-for="creview in review.child">
                            <div class="cavatar">
                                <el-avatar :size="35" :src="creview.avatarUrl+'?t='+new Date().getTime()" />
                            </div>
                            <div class="ccontent">
                                <span class="cusername">{{ creview.username }}
                                <span v-if="creview.type == 2">
                                    回复 {{ creview.parentname }}
                                </span> : &nbsp
                                <span style="color: #000;font-size: 16px;">{{ creview.content }}</span></span> 
                            </div>
                            <div class="cInformation">
                                <div style="float: left;">{{ review.createTime }}</div>
                                <div class="huifu" @click="reply2(creview,index)">回复</div>
                            </div>
                        </div>
                        <div class="review" v-if="showwBackReview === index">
                            <el-input 
                                type="textarea"
                                :placeholder="placeholder" 
                                maxlength="1000" 
                                rows="3"
                                style="width: 600px;margin-top: 10px;margin-left: 10px;height: 80px;" 
                                resize="none"
                                v-model="replyContent"
                            /> 
                            <el-button type="primary" style="margin: 10px;height: 72px;font-weight: normal;" @click="replyView()">发表</el-button>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <div v-if="reviews.length !== 0">
        <el-divider />

        <div class="demo-pagination-block" style="margin: 20px 0;margin-left: 100px;">
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
</template>

<script setup lang="ts">
    import {ref,onBeforeMount,reactive,toRefs, defineProps} from 'vue'
    import axios from 'axios'
    import { ElMessage } from 'element-plus'

    const props = defineProps({
        bookId: Number,
    })

    const {bookId} =toRefs(props)

    const selectNew = ref(true)
    const v = ref(false)
    const myView = ref('')
    const showwBackReview = ref(-1)
    const replyContent = ref('')
    const placeholder = ref('输入评论')

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

    let userInfo = reactive({
        id: 0,
        name: '',
        avatarUrl: ''
    })

    let view = reactive({
        type: 0,
        userId : -1,
        username: '',
        bookId: bookId.value,
        rootId: -1,
        parentId: -1,
        content: ''
    })

    let reviews = reactive([])

    onBeforeMount(() => {
        const token = localStorage.getItem('token')
        axios.get("/user/userInfo").then(response => {
            const data = response.data.data
            if(response.data.result){
                if(data.flag){
                    userInfo = data.data
                    view.userId = userInfo.id
                    view.username = userInfo.name 
                    load()
                }else {
                    ElMessage.error(data.message)
                return;
                }
            }
        })
    })

    function load(){
        v.value = false
        axios.get("/review/getReview", {
            params:{
                bookId : bookId.value,
                pageNum: pageNum.value,
                pageSize: pageSize.value
            }
        }).then(response => {
            console.log(response);
            reviews = response.data.data
            total.value = response.data.total
            setTimeout(()=>{
                v.value = true;
            },80)
        })
    }

    // let view = reactive({
    //     type: 0,
    //     userId : userInfo.id,
    //     bookId: bookId.value,
    //     rootId: -1,
    //     parentId: -1,
    //     content: ''
    // })

    function myViewSend(){
        // 后端设置-1为null
        view.type = 0
        view.rootId = -1
        view.parentId = -1
        view.content = myView.value
        sentView()
    }

    function reply1(data,index){
        showwBackReview.value = index
        placeholder.value = '输入评论'
        replyContent.value = '';
        view.type = 1
        view.rootId = data.id
        view.parentId = data.id
    }

    function reply2(data,index){
        showwBackReview.value = index
        placeholder.value = '回复 ' + data.username + ' : '
        replyContent.value = '';
        view.type = 2
        view.rootId = data.rootId
        view.parentId = data.id
    }

    function replyView(){
        view.content = replyContent.value
        sentView()
    }

    function sentView(){
        console.log(replyContent.value);
        if(view.content === ''){
            ElMessage.error('评论内容不能为空！')
            return false;
        }
        // console.log(view);
        // return

        // 向后端传数据
        axios.post('/review/addReview', view).then(response => {
            replyContent.value = ''
            myView.value = ''
            load()
        })
    }
</script>

<style scoped>
    .allReview{
        display: flex;
        flex-direction: column;
    }
    .header{
        margin-top: 10px;
        margin-bottom: 20px;
    }
    .myView{
        width: 100%;
        height: auto;
        display: flex;
        /* background-color: pink; */
    }

    .demo-basic--circle{
        width: 80px;
        height: 80px;
        margin-left: 20px;
    }

    .demo-basic--circle::after{
        content: '';
        display: block;
        clear: both;
    }

    .write{
        float: left;
        width: 200px;
        margin-left: 14px;
        margin-top: 14px;
    }
    .order:hover{
        cursor: pointer;
    }

    .reviewRoot{
        width: 100%;
        height: auto;
        /* background-color: pink; */
    }

    .reviewRoot::after{
        content: '';
        display: block;
        clear: both;
    }

    .reviewRootName{
        /* padding: 20px; */
        color: #3d3d3d;
        float: left;
        margin-top: -55px;
        margin-left: 100px;
        font-size: 15px;
    }

    .cusername{
        color: #3d3d3d;
        margin-right: 10px;
        font-size: 14px;
    }

    .reviewRootContent{
        width: 700px;
        height: auto;
        margin-left: 100px;
        white-space: pre-wrap;
        float: left;
        margin-top: -20px;
    }

    .information{
        width: 100%;
        color: #3d3d3d;
        font-size: 14px;
        height: 30px;
        /* background-color: aquamarine; */
        line-height: 30px;
        margin-top: 10px;
    }

    .cInformation{
        width: 100%;
        color: #3d3d3d;
        font-size: 14px;
        height: 30px;
        /* background-color: aquamarine; */
        line-height: 30px;
        margin-left: 50px;
    }
    
    .like{
        width: auto;
        height: 20px;
        margin-top: 3px;
    }
    .like:hover{
        cursor: pointer;
    }

    .huifu{
        margin-left: 15px;
        float: left;
    }

    .huifu:hover{
        cursor: pointer;
        color: rgb(0, 162, 255);
    }

    .childrenReview{
        height: auto;
        display: flex;
        /* background-color: rgb(235, 235, 235); */
        flex-direction: column;
        /* padding: 0 20px; */
        /* border-radius: 10px; */
    }

    .creview{
        margin-top: 20px;
    }

    .creview::after{
        content: '';
        display: block;
        clear: both;
    }

    .cavatar{
        float: left;
    }

    .ccontent{
        height: auto;
        width: 650px;
        float: left;
        margin-left: 10px;
        display: flex;
        margin-top: 5px;
        margin-bottom: 10px;
    }

    .review {
        height: 100px;
        /* width: 100%; */
        display: flex;
        margin-top: 20px;
        /* background-color: pink; */
    }
</style>