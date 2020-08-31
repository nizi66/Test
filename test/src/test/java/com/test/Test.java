package com.test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.test.vo.Refuse;
import com.test.vo.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.ObjectUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class Test {

    @org.junit.Test
    public void test() throws Exception {
        //insertText();
        // pdf2png("C:\\Users\\Lenovo\\Desktop","委托协议.pdf");
        Map<String, String> map = new HashMap<>();
        map.put("hospName", "医院名称");
        map.put("deliveryOrgName", "wqe");
        createPDF("test", map);

        //generateFinalPdf("C:\\Users\\Lenovo\\Desktop\\委托协议.pdf","C:\\Users\\Lenovo\\Desktop\\test1.pdf");
    }

    //转换全部的pdf
    public static void pdf2png(String fileAddress, String filename) {
        // 将pdf装图片 并且自定义图片得格式大小
        File file = new File(fileAddress + "\\" + filename);
        try {
            PDDocument doc = PDDocument.load(file);
            PDFRenderer renderer = new PDFRenderer(doc);
            int pageCount = doc.getNumberOfPages();
            for (int i = 0; i < pageCount; i++) {
                BufferedImage image = renderer.renderImageWithDPI(i, 144); // Windows native DPI
                // BufferedImage srcImage = resize(image, 240, 240);//产生缩略图
                ImageIO.write(image, "PNG", new File(fileAddress + "\\" + filename + "_" + (i + 1) + ".png"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 根据模板生成pdf
     *
     * @param pdfName 文件名
     * @param data    Map(String,Object)
     * @return 文件保存全路径文件
     */
    public String createPDF(String pdfName, Map<String, String> data) throws Exception {
        PdfReader reader = null;
        AcroFields s = null;
        PdfStamper ps = null;
        ByteArrayOutputStream bos = null;

/*        String realPath = ResourceBundle.getBundle("systemconfig").getString("upLoadFolder") + File.separator + "comfirmationDoc";
        String dateFolder = DateFormatUtils.format(new Date(), "yyyyMMdd");
        String folderPath = realPath + File.separator + dateFolder;*/
        //创建上传文件目录
      /*  File folder = new File("C:\\Users\\Lenovo\\Desktop\\购销协议.pdf");
        if (!folder.exists()) {
            folder.mkdirs();
        }*/

        try {
            //设置字体
            /*  String font = this.getClass().getClassLoader().getResource("YaHei.ttf").getPath();*/
            reader = new PdfReader("C:\\Users\\Lenovo\\Desktop\\test2.pdf");
            bos = new ByteArrayOutputStream();
            ps = new PdfStamper(reader, bos);
            s = ps.getAcroFields();
            //使用中文字体 使用 AcroFields填充值的不需要在程序中设置字体，在模板文件中设置字体为中文字体 Adobe 宋体 std L
/*
            BaseFont bfChinese = BaseFont.createFont(font, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
*/
            //设置编码格式
/*
            s.addSubstitutionFont(bfChinese);
*/
            // 遍历data 给pdf表单表格赋值

            for (String key : s.getFields().keySet()) {
                if (data.get(key) != null) {
                    s.setField(key, data.get(key));
                }
            }


            // 如果为false那么生成的PDF文件还能编辑，一定要设为true
            ps.setFormFlattening(true);
            ps.close();

       /*     FileOutputStream fos = new FileOutputStream("C:\\Users\\Lenovo\\Desktop\\test4.pdf");
            byte[] bytes = bos.toByteArray();
            fos.write(bos.toByteArray());
            fos.flush();
            fos.close();*/
            PdfReader reader1 = new PdfReader(bos.toByteArray());
            FileOutputStream outputStream = new FileOutputStream("C:\\Users\\Lenovo\\Desktop\\test3.pdf");
            Rectangle pageSize = reader.getPageSize(reader1.getNumberOfPages());
            Document document = new Document(pageSize);
            PdfWriter writer = PdfWriter.getInstance(document, outputStream);
            document.open();
            PdfContentByte cbUnder = writer.getDirectContentUnder();
            for(int i=1;i<=reader1.getNumberOfPages();i++){
                document.newPage();
                PdfImportedPage pageTemplate = writer.getImportedPage(reader1,i);
                cbUnder.addTemplate(pageTemplate, 0, 0);
            }

            document.newPage();//新创建一页来存放后面生成的表格
            Paragraph paragraph = generatePdfATATable();//此处为生成的表格及内容方法，只已ＡＴＡ表为例，其余两个就不写了
            document.add(paragraph);

            document.close();
            outputStream.close();

            return "";
        } catch (IOException | DocumentException e) {
            log.error("读取文件异常");
            e.printStackTrace();
            return "";
        } finally {
            try {
                bos.close();
                reader.close();
            } catch (IOException e) {
                log.error("关闭流异常");
                e.printStackTrace();
            }
        }
    }

    /**
     * 生成最终版本的pdf
     *
     * @param oldPath   已写入数据的pdf模板路径
     * @param finalPath 最终版本的pdf生成路径
     * @param  
     * @throws Exception
     */
    private void generateFinalPdf(String oldPath, String finalPath) throws Exception {
        FileOutputStream outputStream = new FileOutputStream(finalPath);
        PdfReader reader = new PdfReader(oldPath);// 读取pdf模板
        Rectangle pageSize = reader.getPageSize(reader.getNumberOfPages());
        Document document = new Document(pageSize);
        PdfWriter writer = PdfWriter.getInstance(document, outputStream);
        document.open();
        PdfContentByte cbUnder = writer.getDirectContentUnder();
        for (int i = 1; i <= reader.getNumberOfPages(); i++) {
            document.newPage();
            PdfImportedPage pageTemplate = writer.getImportedPage(reader, i);
            cbUnder.addTemplate(pageTemplate, 0, 0);
        }

        document.newPage();//新创建一页来存放后面生成的表格
        // Paragraph paragraph = generatePdfATATable();//此处为生成的表格及内容方法，只已ＡＴＡ表为例，其余两个就不写了
        // document.add(paragraph);

        document.close();
        reader.close();

    }

    /**
     * 生成pdf表格
     *
     * @return
     * @see
     */
    private Paragraph generatePdfATATable() throws Exception {
        BaseFont bfChinese = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
        Font fontChinese = new Font(bfChinese, 10.5F, Font.NORMAL);// 五号
        Paragraph ret = new Paragraph("附表1： 采购医用药品明细表", fontChinese);
        PdfPTable tableBox = new PdfPTable(3);
        tableBox.setWidths(new float[]{0.3f, 0.4f, 0.3f});// 每个单元格占多宽
        tableBox.setWidthPercentage(80f);
       /* tableBox.setTotalWidth(tatalWidth);
        tableBox.writeSelectedRows(1, 5, x, y, under);*/
        // 获取ATA分类的结果集
        User user = new User("小明", "1@qq.com", "123243",1);
        User user1 = new User("小红", "2@qq.com", "12324322",2);
        User user2 = new User("小强", "3@qq.com", "12324322232",3);

        List<User> users = new ArrayList<User>();
        for(int i=0; i<100;i++){
            users.add(user);
            users.add(user1);
            users.add(user2);
        }

        // 创建表格格式及内容
        tableBox.addCell(getCell(new Phrase("基线按ATA章节分类情况", fontChinese), false, 3, 1));
        tableBox.addCell(getCell(new Phrase("ATA", fontChinese), false, 1, 1));
        tableBox.addCell(getCell(new Phrase("文件/图样数量", fontChinese), false, 1, 1));
        tableBox.addCell(getCell(new Phrase("DM数量", fontChinese), false, 1, 1));
        // 遍历查询出的结果
        for (User ata : users) {
            tableBox.addCell(getCell(new Phrase(ata.getUserName(), fontChinese), false, 1, 1));
            tableBox.addCell(getCell(new Phrase(String.valueOf(ata.getEmail()), fontChinese), false, 1, 1));
            tableBox.addCell(getCell(new Phrase(String.valueOf(ata.getPhone()), fontChinese), false, 1, 1));
        }
        ret.add(tableBox);
        return ret;

    }


    private static PdfPCell getCell(Phrase phrase, boolean yellowFlag, int colSpan, int rowSpan) {
        PdfPCell cells = new PdfPCell(phrase);
        cells.setUseAscender(true);
        cells.setMinimumHeight(20f);
        cells.setHorizontalAlignment(1);
        cells.setVerticalAlignment(5);
        cells.setColspan(colSpan);
        cells.setRowspan(rowSpan);
        cells.setNoWrap(false);
        return cells;
    }


    public static void insertText() throws IOException, DocumentException {



        PdfReader reader = new PdfReader("C:\\Users\\Lenovo\\Desktop\\test.pdf");
        PdfStamper ps = new PdfStamper(reader, new FileOutputStream("C:\\Users\\Lenovo\\Desktop\\test2.pdf")); // 生成的输出流

        AcroFields s = ps.getAcroFields();

        List<AcroFields.FieldPosition> list = s.getFieldPositions("CONNECT_NAME");
        Rectangle rect = list.get(0).position;


        PdfContentByte cb = ps.getOverContent(1);
        PdfPTable table = new PdfPTable(1);
        float tatalWidth = rect.getRight() - rect.getLeft() - 1;
        table.setTotalWidth(tatalWidth);


        PdfPCell cell = new PdfPCell(new Phrase(CreateChunk()));
        cell.setFixedHeight(rect.getTop() - rect.getBottom() - 1);
        cell.setBorderWidth(0);
    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        /*    cell.setHorizontalAlignment(Element.ALIGN_CENTER);*/
        cell.setLeading(0, (float) 1.1);


        table.addCell(cell);
        table.writeSelectedRows(0, -1, rect.getLeft(), rect.getTop(), cb);

        ps.close();
        reader.close();
    }

    public static Chunk CreateChunk() {
        BaseFont bfChinese;
        Chunk chunk = null;
        try {
            bfChinese = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.EMBEDDED);
            Font fontChinese = new Font(bfChinese, 10 * 4 / 3);
            chunk = new Chunk("本企业在认真阅读《福建省医疗保障管理委员会办公室关于开展医疗器械（医用耗材）阳光采购结果全省共享工作的通知》（闽医保办﹝2018﹞47号）及《福建省药械采购供应保障不良记录管理办法（试行）》等有关规定后承诺：", fontChinese);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return chunk;
    }

    @org.junit.Test
    public void test1() throws FileNotFoundException {

        List<Integer> list = Arrays.asList(1);
        list.stream().map(a->a*2).collect(Collectors.toList());
        System.out.println(list);
        List<Integer> num = Arrays.asList(1,2,3,4,5);
        List<Integer> collect1 = num.stream().map(n -> n * 2).collect(Collectors.toList());
        System.out.println(num); //[2, 4, 6, 8, 10]


        /*List<User> list = new ArrayList<>();
        User user = new User("1","1","1",1);
        list.add(user);
        Map fieldMap = new HashMap();
        fieldMap.put("userName","userName");
        list.stream().map(o->{
            JSONObject item = (JSONObject) JSONObject.toJSON(o);
            JSONObject data = new JSONObject();
            for(Object key : item.keySet()){
               System.out.println(key);
               if (!Objects.isNull(fieldMap.get(key))) {
                   System.out.println("key is" + key);
                   data.put(key.toString(), item.get(key));
               }
           }
            return JSON.toJavaObject(data, User.class);
        });
        System.out.println(list.get(0).getEmail());*/


   /*     list.stream().map(o -> {
            JSONObject item = (JSONObject) JSONObject.toJSON(o);
            JSONObject data = new JSONObject();
            for (Object key : item.keySet()) {
                if (!Objects.isNull(fieldMap.get(key))) {
                    data.put(key.toString(), item.get(key));
                }else if(!Objects.isNull(fieldMap.get(key.toString()+"Desc"))){
                    //针对枚举字段特殊处理
                    data.put(key.toString(), item.get(key));
                }else if(key.toString().lastIndexOf("Id") != -1){
                    data.put(key.toString(), item.get(key));
                }
            }
            return JSON.toJavaObject(data, User.class);
        }).collect(Collectors.toList());*/
      /*  System.out.println(list.get(0).getEmail());*/
    }

    @org.junit.Test
    public void test2(){
       List<String> list1 = Arrays.asList("1","2","3","4","4");
        List<String> list2 = Arrays.asList("2","3","4");

    }





}
