package com.codeworxzw;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceProcessor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Arrays;
import java.util.Collection;


@EnableAsync
@SpringBootApplication
public class DemoSpringBootApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoSpringBootApplication.class, args);
    }


    @Bean
    CommandLineRunner runner(ReservationRepository rr) {
        return args -> {
            Arrays.asList("Fidelis,Samuel,Shamiso,Catherine".split(",")).forEach(n -> rr.save(new Reservation(n)));
            rr.findAll().forEach(System.out::println);

            System.out.println("Find by name");
            rr.findByReservationName("Shamiso").forEach(System.out::println);
            System.out.println("----END-----");
        };
    }
}

@RestController
class ReservationRestController{

    @RequestMapping("/reservations")
    Collection<Reservation> reservations(){
        return this.reservationRepository.findAll(new Sort(Sort.Direction.ASC, "reservationName"));
    }

    @Autowired
    ReservationRepository reservationRepository;

}

@Component
class ReservationResourceProcessor implements ResourceProcessor<Resource<Reservation>>{

    @Override
    public Resource<Reservation> process(Resource<Reservation> reservationResource) {
        reservationResource.add(new Link("http://ssmgwokuda.com/imgs/"+ reservationResource.getContent().getId()+
        ".jpg", "profile-photo"));
        return reservationResource;
    }
}

@Controller
class ReservationMVCController{
    @RequestMapping("/reservations.php")
    String reservations(Model model){
        model.addAttribute("reservations", this.reservationRepository.findAll(new Sort(Sort.Direction.DESC, "reservationName")));
        return "reservations"; //src/main/resources/templates/ + $X + .html
    }

    @Autowired
    ReservationRepository reservationRepository;
}



@RepositoryRestResource
    interface ReservationRepository extends JpaRepository<Reservation, Long>{
        Collection<Reservation> findByReservationName(@Param("rn") String rn);
    }

    @Entity
    class Reservation{
        @Id
        @GeneratedValue
        private Long id;

        private String reservationName;

        public Reservation(){
        }

        public Reservation(String reservationName){
           this.reservationName = reservationName;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getReservationName() {
            return reservationName;
        }

        public void setReservationName(String reservationName) {
            this.reservationName = reservationName;
        }

        @Override
        public String toString() {
            return "Reservation{" +
                    "id=" + id +
                    ", reservationName='" + reservationName + '\'' +
                    '}';
        }
    }





