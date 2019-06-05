package swallow3.sample.test;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import lombok.extern.slf4j.Slf4j;
import swallow3.mybatisplus.SwallowMapper;

@Slf4j
@SpringBootApplication
@MapperScan(value = {"swallow3.sample.test"})
public class DemoApplication implements CommandLineRunner{

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@Bean
	public PaginationInterceptor paginationInterceptor() {
		return new PaginationInterceptor();
	}

	

	@Autowired
	private StudentMapper mpp;
	
	@Autowired
	private StudentService service;

	@Override
	public void run(String... args) throws Exception {
		QueryWrapper<Student> query= Wrappers.<Student>query()
			.gt(StudentMeta.id, 2)
			.or()
			.isNotNull(StudentMeta.className);

		
		service.list(query).forEach(System.out::println);

		service.page(new Page(1,10));
		//System.out.println(b);

	}

}
