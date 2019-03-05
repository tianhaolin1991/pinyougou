/*import com.pinyougou.pojo.TbBrand;
import com.pinyougou.sellergoods.service.BrandService;
import javafx.application.Application;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="classpath*:spring/applicationContext*.xml")
public class MyTest {

    @Test
    public void test01(){
        ApplicationContext ac = new ClassPathXmlApplicationContext("classpath*:spring/applicationContext*.xml");
        BrandService brandService = ac.getBean("brandServiceImpl", BrandService.class);
        List<TbBrand> all = brandService.findAll();
        for (TbBrand tbBrand : all) {
            System.out.println(tbBrand);
        }
    }

}*/
