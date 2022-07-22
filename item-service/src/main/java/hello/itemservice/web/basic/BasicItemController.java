package hello.itemservice.web.basic;

import hello.itemservice.domain.item.Item;
import hello.itemservice.domain.item.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.util.List;

@Controller
@RequestMapping("basic/items")
@RequiredArgsConstructor // final 붙은것으로 생성자만듬
public class BasicItemController {

    private final ItemRepository itemRepository;

//    @Autowired // 생성자가 하나면 생략가능
//    public BasicItemController(ItemRepository itemRepository) {
//        this.itemRepository = itemRepository;
//    } @RequiredArgsConstructor

    @GetMapping
    public String items(Model model) {
        List<Item> items = itemRepository.findAll();
        model.addAttribute("items", items);
        return "basic/items";
    }

    @GetMapping("/{itemId}")
    public String item(@PathVariable long itemId, Model model){
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "basic/item";
    }

    @GetMapping("/add")
    public String addForm() {
        return "basic/addForm";
    }

    //@PostMapping("/add")
    public String addItemV1(@RequestParam String itemName,
                       @RequestParam int price,
                       @RequestParam Integer quantity,
                       Model model) {

        Item item = new Item();
        item.setItemName(itemName);
        item.setPrice(price);
        item.setQuantity(quantity);

        itemRepository.save(item);

        model.addAttribute("item", item);
        return "basic/item";
    }

    //@PostMapping("/add")
    public String addItemV2(@ModelAttribute("item") Item item) {

//        Item item = new Item(); @ModelAttribute 가 set 으로 다 호출해줌
//        item.setItemName(itemName);
//        item.setPrice(price);
//        item.setQuantity(quantity);

        itemRepository.save(item);

//        model.addAttribute("item", item); @ModelAttribute 가 얘까지 자동호출해줌
        return "basic/item";
    }

    //@PostMapping("/add") // @ModelAttribute(...)을 생략하면 클래스명의 첫글자를 소문자로 바꿔서 모델애트리뷰트에 넣어줌
    public String addItemV3(@ModelAttribute Item item) {
        itemRepository.save(item);
        return "basic/item";
    }

    @PostMapping("/add") // 생략하면 클래스명의 첫글자를 소문자로 바꿔서 모델애트리뷰트에 넣어줌
    public String addItemV4(Item item) {
        itemRepository.save(item);
        return "basic/item";
    }

    // 테스트용 데이터 추가
    @PostConstruct
    public void init() {
        itemRepository.save(new Item("itemA", 10000, 10));
        itemRepository.save(new Item("itemB", 20000, 20));
    }
}
