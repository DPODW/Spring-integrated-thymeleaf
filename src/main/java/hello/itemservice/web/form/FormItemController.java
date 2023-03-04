package hello.itemservice.web.form;

import hello.itemservice.domain.item.DeliveryCode;
import hello.itemservice.domain.item.Item;
import hello.itemservice.domain.item.ItemRepository;
import hello.itemservice.domain.item.ItemType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/form/items")
@RequiredArgsConstructor
public class FormItemController {

    private final ItemRepository itemRepository;

    @ModelAttribute("regions")
    /**
     * 여기서 사용되는 @ModelAttribute 는 우리가 아는 매개값 @ModelAttribute 와는 다른 기능이다.
     * 해당 어노테이션이 상단에 붙게되면, 해당되는 로직들은 앱이 실행되면 무조건 model 에 값이 담기게 된다.
     * [[ 아무 상황에서나 model 에서 값을 빼서 쓸수 있게 되는 것이다. (물론 정적인 값이 들어가야겠지?) ]]
     * regions 라는 model 을 새로 만들어서 값을 put 한 다음 ,체크 박스를 만드는 형식
     * */
    public Map<String, String> regions() {
        Map<String, String> regions = new LinkedHashMap<>(); //순서를 보장하면서 저장하려면 LinkedHashMap 을 사용해야한다.
        regions.put("SEOUL", "서울");
        regions.put("BUSAN","부산");
        regions.put("JEJU", "제주");
        return regions;
        //고정적인 값이 저장되는 로직이니까 static class 로 따로 분리해서 설계하는것이 이상적 (아래도 동일)
    }

    /**
     * ItemType enum class 를 배열 타입으로 지정한다.
     * 그리고 .values 를 사용하게 되면, 배열로 받아낼수 있다. (enum class 의 특징)
     * */
    @ModelAttribute("itemType")
    public ItemType[] itemTypes(){
        return ItemType.values();
    }
    
    @ModelAttribute("deliveryCodes")
    public List<DeliveryCode> deliveryCodes() {
        List<DeliveryCode> deliveryCodes = new ArrayList<>();
        deliveryCodes.add(new DeliveryCode("FAST", "빠른 배송"));
        deliveryCodes.add(new DeliveryCode("NORMAL", "일반 배송"));
        deliveryCodes.add(new DeliveryCode("SLOW", "느린 배송"));
        return deliveryCodes;
    }

    @GetMapping
    public String items(Model model) {
        List<Item> items = itemRepository.findAll();
        model.addAttribute("items", items);
        return "form/items";
    }

    @GetMapping("/{itemId}")
    public String item(@PathVariable long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "form/item";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("item",new Item());
        return "form/addForm";
    }

    @PostMapping("/add")
    public String addItem(@ModelAttribute Item item, RedirectAttributes redirectAttributes) {
        log.info("item.open={}",item.getOpen());
        log.info("item.regions={}",item.getRegions());
        log.info("item.itemType={}",item.getItemType());

        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/form/items/{itemId}";
    }

    @GetMapping("/{itemId}/edit")
    public String editForm(@PathVariable Long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);

        return "form/editForm";
    }

    @PostMapping("/{itemId}/edit")
    public String edit(@PathVariable Long itemId, @ModelAttribute Item item) {
        itemRepository.update(itemId, item);
        return "redirect:/form/items/{itemId}";
    }

}

