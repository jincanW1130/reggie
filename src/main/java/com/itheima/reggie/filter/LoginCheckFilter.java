package com.itheima.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 检查用户是否已经完成登录
 *
 * 处理规则：
 * 1. 登录/退出接口、登录页面及其静态资源直接放行；
 * 2. 未登录访问后台其他页面时，直接重定向到登录页面；
 * 3. 未登录访问业务接口(ajax)时，返回 NOTLOGIN 结果，
 *    由前端 request.js 统一拦截并跳转到登录页面。
 */
@WebFilter(filterName = "loginCheckFilter",urlPatterns = "/*")
@Slf4j
public class LoginCheckFilter implements Filter{
    //路径匹配器，支持通配符
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        //1、获取本次请求的URI
        String requestURI = request.getRequestURI();// /backend/index.html

        log.info("拦截到请求：{}",requestURI);

        //定义不需要处理的请求路径
        //说明：/backend 下的页面均为静态页面(html)，真正的权限校验发生在页面发起的 ajax 请求上，
        //     所以这里放行登录页及登录页依赖的公共静态资源(js/css/图片)，避免登录页样式错乱；
        //     其余后台页面(如 /backend/index.html)若未登录，则重定向到登录页。
        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
//                "/backend/**",

                "/backend/page/login/**",   //登录页
                "/backend/api/**",          //前端接口封装的js静态资源
                "/backend/images/**",       //图片静态资源
                "/backend/js/**",           //js静态资源
                "/backend/plugins/**",      //第三方插件(vue/element-ui/axios等)
                "/backend/styles/**",       //样式静态资源
                "/backend/favicon.ico",     //网站图标
                "/front/**"                 //移动端前端页面(C端登录后续课程实现)
        };

        //2、判断本次请求是否需要处理
        boolean check = check(urls, requestURI);

        //3、如果不需要处理，则直接放行
        if(check){
            log.info("本次请求{}不需要处理",requestURI);
            filterChain.doFilter(request,response);
            return;
        }

        //4、判断登录状态，如果已登录，则直接放行
        if(request.getSession().getAttribute("employee") != null){
            log.info("用户已登录，用户id为：{}",request.getSession().getAttribute("employee"));
            //将当前登录用户的id存入ThreadLocal(公共字段自动填充等场景使用)
            Long empId = (Long) request.getSession().getAttribute("employee");
            BaseContext.setCurrentId(empId);
            try {
                filterChain.doFilter(request,response);
            } finally {
                //请求处理完毕后移除，避免Tomcat线程复用造成数据串扰
                BaseContext.removeCurrentId();
            }
            return;
        }

        log.info("用户未登录");

        //5、如果未登录，判断本次请求的类型
        //  5.1 访问后台页面(html)时，直接重定向到登录页面
        if(requestURI.startsWith("/backend")){
            log.info("未登录访问页面，重定向到登录页：{}",requestURI);
            response.sendRedirect("/backend/page/login/login.html");
            return;
        }
        //  5.2 访问业务接口时，通过输出流返回未登录结果，由前端拦截处理
        response.setContentType("application/json;charset=utf-8");
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;
    }

    /**
     * 路径匹配，检查本次请求是否需要放行
     * @param urls
     * @param requestURI
     * @return
     */
    public boolean check(String[] urls,String requestURI){
        for (String url : urls) {
            boolean match = PATH_MATCHER.match(url, requestURI);
            if(match){
                return true;
            }
        }
        return false;
    }
}
