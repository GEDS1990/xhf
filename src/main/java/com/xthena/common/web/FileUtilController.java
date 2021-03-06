package com.xthena.common.web;

import com.xthena.jl.manager.JlDeptManager;
import com.xthena.security.util.SpringSecurityUtils;
import com.xthena.util.ConfUtil;
import com.xthena.util.FileUtil;
import com.xthena.util.JsonResponseUtil;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

@Controller
@RequestMapping("comm")
public class FileUtilController {

    @Autowired
    private JlDeptManager jlDeptManager;

    @RequestMapping("comm-uploadFile")
    public void uploadImage(@RequestParam("files[]") MultipartFile attachment,
                            HttpServletRequest request,
                            HttpServletResponse httpServletResponse) throws Exception {
        String uploadFileName = attachment.getOriginalFilename();
        String srcFileName = null;
        String fileType = null;

        String filePath = "";
        /*//页面定义目录
        if(filePath==null){
    		filePath="";
    	}else if(filePath!=null&&!filePath.endsWith("/")){
    		filePath=filePath+"/";
    	}*/
        filePath = request.getSession().getServletContext().getRealPath(ConfUtil.getProperty("upload.dir"));

        if (uploadFileName.indexOf(".") > 0) {
            int start = uploadFileName.lastIndexOf(".");
            srcFileName = uploadFileName.substring(0, start);
            fileType = uploadFileName.substring(start + 1, uploadFileName.length());
        } else {
            srcFileName = uploadFileName;
            fileType = "";
        }

        String fileName;
        if (FileUtil.isVideo(uploadFileName)) {
            long fxmid = jlDeptManager.getXmId(request);
            fileName = "default_jl_video_" + fxmid;
        } else {
            fileName = String.valueOf(System.currentTimeMillis());
            Random rand = new Random();
            int r = rand.nextInt(100);
            fileName = fileName + "_" + String.format("%03d", r);
        }

        String simpleNewFilename = fileName + "." + fileType;

        File file = new File(filePath + File.separator + simpleNewFilename);

        if(file.exists()) {
            file.delete();
        }

        attachment.transferTo(file);

        if (needCompress(file)) {
            Thumbnails.of(file).size(1024, 768).toFile(file);
        }

        HashMap<String, String> fileMap = new HashMap<String, String>();
        fileMap.put("fileName", srcFileName);
        fileMap.put("fileUrl", ConfUtil.getProperty("download.url") + simpleNewFilename);
        fileMap.put("fileType", fileType);
        fileMap.put("uploaduser", SpringSecurityUtils.getCurrentUserId());
        List<HashMap<String, String>> filesHashMaps = new ArrayList<HashMap<String, String>>();
        filesHashMaps.add(fileMap);

        HashMap<String, List<HashMap<String, String>>> result = new HashMap<String, List<HashMap<String, String>>>();
        result.put("files", filesHashMaps);

        JsonResponseUtil.write(httpServletResponse, result);
    }


    @RequestMapping("editor-article-uploadImage")
    public void uploadImage1(@RequestParam("CKEditorFuncNum") String callback,
                             @RequestParam("upload") MultipartFile attachment, HttpServletResponse httpServletResponse) throws Exception {

        String uploadFileName = attachment.getOriginalFilename();
        String srcFileName = null;
        String fileType = null;

        String filePath = "";
        /*//页面定义目录
        if(filePath==null){
    		filePath="";
    	}else if(filePath!=null&&!filePath.endsWith("/")){
    		filePath=filePath+"/";
    	}*/

        if (uploadFileName.indexOf(".") > 0) {
            int start = uploadFileName.lastIndexOf(".");
            srcFileName = uploadFileName.substring(0, start);
            fileType = uploadFileName.substring(start + 1, uploadFileName.length());
        } else {
            srcFileName = uploadFileName;
            fileType = "";
        }
        String fileName = String.valueOf(System.currentTimeMillis());
        String simpleNewFilename = fileName + "." + fileType;
        File file = new File(ConfUtil.getProperty("upload.dir") + filePath + simpleNewFilename);
        attachment.transferTo(file);
        HashMap<String, String> fileMap = new HashMap<String, String>();
        fileMap.put("fileName", srcFileName);
        fileMap.put("fileUrl", ConfUtil.getProperty("download.url") + filePath + simpleNewFilename);
        fileMap.put("fileType", fileType);
        fileMap.put("uploaduser", SpringSecurityUtils.getCurrentUserId());
        List<HashMap<String, String>> filesHashMaps = new ArrayList<HashMap<String, String>>();
        filesHashMaps.add(fileMap);

        HashMap<String, List<HashMap<String, String>>> result = new HashMap<String, List<HashMap<String, String>>>();
        result.put("files", filesHashMaps);

        JsonResponseUtil.write(httpServletResponse, "<script type='text/javascript'>"
                + "window.parent.CKEDITOR.tools.callFunction(" + callback
                + ",'" + ConfUtil.getProperty("download.url") + filePath + simpleNewFilename + "','')"
                + "</script>");
    }

    public boolean needCompress(File mf) throws IOException {
        return (FileUtil.isImage(mf) && mf.length() > 1048576);
    }


}


