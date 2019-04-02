package com.pinyougou.shop.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.orders.service.OrderService;
import com.pinyougou.pojo.TbOrder;
import com.pinyougou.pojogroup.OrderExport;
import entity.PageResult;
import entity.Result;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/order")
public class OrderController {

    @Reference
    private OrderService orderService;

    @RequestMapping("/findNotSendOrdersBySeller")
    @ResponseBody
    public PageResult findNotSendOrdersBySeller(Integer pageNum, Integer pageSize) {
        String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
        return orderService.findNotSendOrdersBySeller(sellerId, pageNum, pageSize);

    }

    @RequestMapping("/sendGoods")
    @ResponseBody
    public Result sendGoods(String orderId) {
        String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
        TbOrder order = orderService.findOne(orderId);
        String realSeller = order.getSellerId();
        if (!sellerId.equals(realSeller)) {
            return new Result(false, "非法操作");
        }

        try {
            orderService.sendGoods(orderId);
            return new Result(true, "发货成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "发货失败");
        }
    }


    @RequestMapping("/getReport")
    @ResponseBody
    public void getRepoert(HttpServletResponse response, String status, Date startTime, Date endTime) {
        try {
            String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
            List<OrderExport> orderExportList = orderService.findOrderListByTimeAreaAndStatus(sellerId, status, startTime, endTime);
            exportOrderExcel(response, orderExportList, startTime, endTime, status);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    private void exportOrderExcel(HttpServletResponse response, List<OrderExport> orderExportList, Date startTime, Date endTime, String status) throws IOException {

        //1:创建一个工作簿(即excel文档)
        XSSFWorkbook xssfWorkbook = new XSSFWorkbook();
        //2:创建工作表 sheel页
        XSSFSheet xssfSheet = xssfWorkbook.createSheet("订单报表"); //指定sheet页名称为:订单报表

        // 准备表头数据
        String[] header = {"支付类型", "订单状态", "下单用户账号", "收货人", "收货人地址", "收货人电话", "订单来源", "商品标题",
                "商品单价", "购买数量", "金额小计", "所属品牌", "一级分类名称", "二级分类名", "三级分类名称", "下单时间"};
        // 准备表体数据

        //3:创建表头
        //3.1创建表头行
        XSSFRow headerRow = xssfSheet.createRow(0);// 创建第一行
        //3.2创建表头所有列
        for (int i = 0; i < header.length; i++) {
            //创建每一列
            XSSFCell xssfCell = headerRow.createCell(i);
            //为每个单元格填充值
            xssfCell.setCellValue(header[i]);
        }

        //4:创建数据体行 创建没行对应的列 并为每列赋值
        for (int i = 0; i < orderExportList.size(); i++) {
            OrderExport orderExport = orderExportList.get(i);
            XSSFRow row = xssfSheet.createRow(i + 1); //创建行-----注意这里要从第二行开始创建 因为刚开始表头已经占了了一行

            XSSFCell paymentTypeCell = row.createCell(0); //支付类型
            paymentTypeCell.setCellValue(orderExport.getPaymentType());

            XSSFCell statusCell = row.createCell(1);//订单状态
            statusCell.setCellValue(orderExport.getStatus());

            XSSFCell userIdCell = row.createCell(2);//下单用户账号
            userIdCell.setCellValue(orderExport.getUserId());

            XSSFCell receiverCell = row.createCell(3); //收货人
            receiverCell.setCellValue(orderExport.getReceiver());

            XSSFCell receiverAreaNameCell = row.createCell(4);//收货人地址
            receiverAreaNameCell.setCellValue(orderExport.getReceiverAreaName());

            XSSFCell receiverMobileCell = row.createCell(5);//收货人电话
            receiverMobileCell.setCellValue(orderExport.getReceiverMobile());

            XSSFCell sourceTypeCell = row.createCell(6);//订单来源
            sourceTypeCell.setCellValue(orderExport.getSourceType());

            XSSFCell titleCell = row.createCell(7);//商品标题
            titleCell.setCellValue(orderExport.getTitle());

            XSSFCell priceCell = row.createCell(8);//商品单价
            priceCell.setCellValue(orderExport.getPrice() + "");

            XSSFCell numCell = row.createCell(9);//购买数量
            numCell.setCellValue(orderExport.getNum());

            XSSFCell totalFeeCell = row.createCell(10);//金额小计
            totalFeeCell.setCellValue(orderExport.getTotalFee() + "");

            XSSFCell brandNameCell = row.createCell(11);//所属品牌
            brandNameCell.setCellValue(orderExport.getBrandName());

            XSSFCell category1NameCell = row.createCell(12);//一级分类名称
            category1NameCell.setCellValue(orderExport.getCategory1Name());

            XSSFCell category2NameCell = row.createCell(13);//二级分类名称
            category2NameCell.setCellValue(orderExport.getCategory2Name());

            XSSFCell category3NameCell = row.createCell(14);//三级分类名称
            category3NameCell.setCellValue(orderExport.getCategory3Name());

            XSSFCell createTimeCell = row.createCell(15);//下单时间
            createTimeCell.setCellValue(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(orderExport.getCreateTime()));


        }

        String statusStr;
        switch (status) {
            case "1":
                statusStr = "未付款";
                break;
            case "2":
                statusStr = "代发货";
                break;
            case "3":
                statusStr = "已取消";
                break;
            case "4":
                statusStr = "已发货";
                break;
            case "5":
                statusStr = "已收货";
                break;
            default:
                statusStr = "所有";
        }
        //使用字节流输出
        //定义文件名
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String fileName = simpleDateFormat.format(startTime) + "至" + simpleDateFormat.format(endTime) + statusStr + "订单报表.xlsx";
        //设置文件下载的响应头
        response.setContentType("application/vnd.ms-excel;charset=utf-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + new String((fileName).getBytes(), "iso-8859-1"));
        //使用工作簿对象进行输出到字节流  这里的字节流是response对象提供
        xssfWorkbook.write(response.getOutputStream());
        //关闭
        xssfWorkbook.close();

    }


}
