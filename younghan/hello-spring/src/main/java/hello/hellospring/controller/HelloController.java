package hello.hellospring.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HelloController {

    @GetMapping("hello")    // "/hello"로 들어오면
    public String hello(Model model) {
        model.addAttribute("data", "spring!!");  // 치환해줌!
        return "hello"; // resources:templates/ +  {ViewName: hello} + .html 찾아서 템플릿 엔진이 처리해준다.
    }

    // 템플릿 엔진 조작
    @GetMapping("hello-mvc")
    public String helloMvc(@RequestParam(value = "name", required = true) String name, Model model) {
        model.addAttribute("name", name);
        return "hello-template";
    }

    // 데이터 그대로 내려줌
    @GetMapping("hello-string")
    @ResponseBody
    public String helloString(@RequestParam("name") String name) {
        return "hello" + name; // hello spring
    }

    // API: JSON(key-value) 내려줌
    @GetMapping("hello-api")
    @ResponseBody  // JSON으로 반환하는게 기본 (xml ㄴㄴ)
    public Hello helloApi(@RequestParam("name") String name) {
        Hello hello = new Hello();
        hello.setName(name);
        return hello;
    }

    static class Hello {
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
