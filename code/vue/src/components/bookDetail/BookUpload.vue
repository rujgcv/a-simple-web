<template>
    <div class="all" v-show="!v">
        <el-skeleton :rows="50" />
    </div>
    <div class="all" v-show="v">
        <div class="downLoad">
            <el-table :data="dataList" style="width: 100%">
                <el-table-column prop="book_name" label="书名" width="250" />
                <el-table-column prop="create_time" label="发布日期" width="200" />
                <el-table-column label="贡献者" width="250" >
                    <template v-slot="scope">
                        <el-avatar :size="30" :src="scope.row.avatar_url+'?t=' + new Date().getTime()" style="float: left;"/>
                        <div class="uname">{{ scope.row.user_name }}</div>
                    </template>
                </el-table-column>
                <el-table-column label="操作" >
                    <template v-slot="scope">
                        <span class="xz" @click="download(scope.row)">下载</span>
                    </template>
                </el-table-column>
            </el-table>
        </div>
        <!-- <el-divider /> -->
        <div class="upload">
            <div style="margin: 20px;">
                <el-upload
                    ref="upload"
                    class="upload-demo"
                    action="http://localhost:8080/book/text/upload"
                    :limit="1"
                    :data="dataUpload"
                    :on-exceed="handleExceed"
                    :auto-upload="false"
                    :before-upload="beforeFileUpload"
                    :on-success="handleFileSuccess"
                >
                    <template #trigger>
                    <el-button type="primary" style="margin-right: 10px;">选择文件</el-button>
                    </template>
                    <el-button class="ml-3" type="success" @click="submitUpload">
                    点击上传
                    </el-button>
                    <template #tip>
                    <div class="el-upload__tip text-red">
                        最多上传1个文件,且只能上传TXT文件,文件小于10MB
                    </div>
                    </template>
                </el-upload>
            </div>
        </div>
    </div>
</template>

<script setup lang="ts">
    import {ref,onBeforeMount,reactive,toRefs, defineProps} from 'vue'
    import axios from 'axios'
    import { genFileId,ElMessage } from 'element-plus'
    import type { UploadInstance, UploadProps, UploadRawFile } from 'element-plus'
    import { UploadFilled } from '@element-plus/icons-vue'

    const props = defineProps({
        bookId: Number,
        userId: Number
    })

    const v = ref(false)

    let dataList = reactive([{
        book_name: '',
        user_name: '',
        avatar_url: '',
        url: '',
        create_time: ''
    }])
    
    const {bookId} =toRefs(props)
    const {userId} =toRefs(props)

    const dataUpload = reactive({
        bookId: bookId?.value,
        userId: userId?.value
    })

    const upload = ref<UploadInstance>()

    const handleExceed: UploadProps['onExceed'] = (files) => {
        upload.value!.clearFiles()
        const file = files[0] as UploadRawFile
        file.uid = genFileId()
        upload.value!.handleStart(file)
    }

    const handleFileSuccess: UploadProps['onSuccess'] = (
        response,
        uploadFile
    ) => {
        console.log(response);
        if(response.result){
            if(response.data.flag){
            ElMessage({
                message: response.data.message,
                type: 'success',
            })
            load();
            }else {
            ElMessage.error(response.data.message)
            return;
            }
        }else {
            ElMessage.error("上传失败！")
            return;
        }
    }

    const submitUpload = () => {
        upload.value!.submit()
    }

    const beforeFileUpload: UploadProps['beforeUpload'] = (rawFile) => {
        console.log(rawFile.type);
        if (rawFile.type !== 'text/plain') {
            ElMessage.error('文件必须是TXT格式!')
            return false
        } else if (rawFile.size / 1024 / 1024 > 10) {
            ElMessage.error('文件大小不能超过10MB!')
            return false
        }
        return true
    }

    const isOnLine = ref(false)

    function download(data){
        if(data.url === '' || data.url === undefined){
            ElMessage.error('未知错误!')
            return false
        }
        // axios.get("/book/txt/download",{params:{url:data.url,isOnLine : isOnLine.value}}).then(response => {
        //     console.log(response);
        // })
        window.open('http://127.0.0.1:8080/book/txt/download?url='+data.url + '&isOnLine=' + isOnLine.value, '_blank');        
    }

    onBeforeMount(() => {
        load()
    })

    function load(){
        v.value = false
        axios.get("/book/txt/getAll",{params:{id: bookId?.value}}).then(response => {
            dataList = response.data
            console.log(dataList);
            for(var i=0;i<dataList.length;i++){
                dataList[i].book_name = "《"+dataList[i].book_name+"》"
                dataList[i].create_time = dataList[i].create_time.substring(0,10);
            }
            console.log("+==========");
            console.log(dataList);
            setTimeout(() => {
                v.value = true
            },100)
        })
    }
</script>

<style scoped>
    .all{
        margin: 10px 20px;
        /* margin-left: 20px; */
    }
    .upload{
        height: 200px;
    }
    .xz{
        color: rgb(44, 167, 255);
    }
    .xz:hover{
        cursor: pointer;
        color: rgb(33, 112, 169);
        text-decoration: underline;
    }
    .uname{
        width: 180px;
        float: left;
        margin-left: 5px;
        margin-top: 2px;
        text-overflow: ellipsis;
        overflow: hidden;
        word-break: break-all;
        white-space: nowrap;
    }
</style>