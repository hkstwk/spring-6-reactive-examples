
package guru.springframework.spring6reactiveexamples.repositories;

import guru.springframework.spring6reactiveexamples.domain.Person;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PersonRepositoryImplTest {

    PersonRepository personRepository = new PersonRepositoryImpl();

    @Test
    void testMonoByIdBlock() {
        Mono<Person> personMono = personRepository.getById(1);

        Person person = personMono.block();

        System.out.println(
                person.toString()
        );
    }

    @Test
    void testGetByIdSubscriber() {
        Mono<Person> personMono = personRepository.getById(1);

        personMono.subscribe(person -> System.out.println(person.toString()));
    }

    @Test
    void testMapOperation() {
        Mono<Person> personMono = personRepository.getById(1);

        personMono.map(Person::getFirstName).subscribe(System.out::println);
    }

    @Test
    void testFluxBlockFirst() {
        Flux<Person> personFlux = personRepository.findAll();

        Person person = personFlux.blockFirst();

        System.out.println(person);
    }

    @Test
    void testFluxSubscriber() {
        Flux<Person> personFlux = personRepository.findAll();

        personFlux.subscribe(System.out::println);
    }

    @Test
    void testFluxMap() {
        Flux<Person> personFlux = personRepository.findAll();

        personFlux.map(Person::getFirstName).subscribe(System.out::println);
    }

    @Test
    void testFluxToList() {
        Flux<Person> personFlux = personRepository.findAll();

        Mono<List<Person>> listMono = personFlux.collectList();

        listMono.subscribe(list -> list.forEach(person -> {
            System.out.println(person.getFirstName());
        }));
    }

    @Test
    void testFilterOnName() {
        personRepository.findAll()
                .filter(person -> person.getFirstName().equalsIgnoreCase("fiona"))
                .subscribe(person -> System.out.println(person.getFirstName()));
    }

    @Test
    void testGetById() {
        Mono<Person> fionaMono = personRepository.findAll().filter(person -> person.getFirstName().equalsIgnoreCase("fiona")).next();

        fionaMono.subscribe(person -> {
            System.out.println(person.getFirstName());
        });
    }


    @Test
    void testFindPersonByIdNotFound() {
        Flux<Person> personFlux = personRepository.findAll();

        final Integer id = 9;

        Mono<Person> personMono = personFlux.filter(person -> person.getId().equals(id)).single().doOnError(throwable -> {
            System.out.println("error fetching person in flux");
            System.out.println(throwable.toString());
        });

        personMono.subscribe(System.out::println, throwable -> {
            System.out.println("error fetching person in mono");
            System.out.println(throwable.toString());
        });
    }

    @Test
    void testFindByIdNotFound() {
        Mono<Person> personMono = personRepository.getById(9);
        assertEquals(Boolean.FALSE, personMono.hasElement().block());
    }

    @Test
    void testFindByIdFound() {
        Mono<Person> personMono = personRepository.getById(2);
        assertEquals(Boolean.TRUE, personMono.hasElement().block());
    }
}
