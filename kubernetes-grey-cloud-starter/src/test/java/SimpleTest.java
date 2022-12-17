import java.util.List;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author Freeman
 * @since 2022/12/17
 */
public class SimpleTest {

    @Test
    void testMono() {
        Mono<Boolean> mono = Flux.fromIterable(List.of(1, 2, 3))
                .concatMap(rule -> Mono.fromCallable(() -> {
                    System.out.println(rule);
                    return rule % 2 == 0;
                }))
                .filter(Boolean::booleanValue)
                .next()
                .switchIfEmpty(Mono.just(false));
        mono.subscribe(System.out::println);
    }
}
