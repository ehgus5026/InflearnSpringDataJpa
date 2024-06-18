package Study.data_jpa.repository;

import Study.data_jpa.entity.Item;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ItemRepositoryTest {

    @Autowired
    ItemRepository itemRepository;

    @Test
    // 트랜잭션 없어도 JpaRepository 구현체인 SimpleJpaRepository에 트랜잭션이 달려 있어서 자동으로 됨.
    public void save() {
        Item item = new Item("A");
        itemRepository.save(item); // merge()로 넘어가서 db에서 select 날려서 찾아보는데, 없어서 새 거로 insert 쳐서 넣어버림.(비효율)
    }

}