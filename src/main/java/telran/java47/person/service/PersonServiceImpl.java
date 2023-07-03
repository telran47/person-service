package telran.java47.person.service;

import java.time.LocalDate;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import telran.java47.person.dao.PersonRepository;
import telran.java47.person.dto.AddressDto;
import telran.java47.person.dto.CityPopulationDto;
import telran.java47.person.dto.PersonDto;
import telran.java47.person.dto.exceptions.PersonNotFoundException;
import telran.java47.person.model.Address;
import telran.java47.person.model.Person;

@Service
@RequiredArgsConstructor
public class PersonServiceImpl implements PersonService {

	final PersonRepository personRepository;
	final ModelMapper modelMapper;

	@Override
	@Transactional
	public Boolean addPerson(PersonDto personDto) {
		if (personRepository.existsById(personDto.getId())) {
			return false;
		}
		personRepository.save(modelMapper.map(personDto, Person.class));
		return true;
	}

	@Override
	public PersonDto findPersonById(Integer id) {
		Person person = personRepository.findById(id).orElseThrow(PersonNotFoundException::new);
		return modelMapper.map(person, PersonDto.class);
	}

	@Override
	@Transactional
	public PersonDto removePerson(Integer id) {
		Person person = personRepository.findById(id).orElseThrow(() -> new PersonNotFoundException());
		personRepository.delete(person);
		return modelMapper.map(person, PersonDto.class);
	}

	@Override
	@Transactional
	public PersonDto updatePersonName(Integer id, String name) {
		Person person = personRepository.findById(id).orElseThrow(() -> new PersonNotFoundException());
		person.setName(name);
//		personRepository.save(person);
		return modelMapper.map(person, PersonDto.class);
	}

	@Override
	@Transactional
	public PersonDto updatePersonAddress(Integer id, AddressDto addressDto) {
		Person person = personRepository.findById(id).orElseThrow(() -> new PersonNotFoundException());
		person.setAddress(modelMapper.map(addressDto, Address.class));
//		personRepository.save(person);
		return modelMapper.map(person, PersonDto.class);
	}

	@Override
	public Iterable<PersonDto> findPersonsByCity(String city) {
		return personRepository.findAll()
				.stream()
				.filter(p -> p.getAddress().getCity().equalsIgnoreCase(city))
				.map(p -> modelMapper.map(p, PersonDto.class))
				.collect(Collectors.toList());
	}

	@Override
	public Iterable<PersonDto> findPersonsByName(String name) {
		return personRepository.findAll()
				.stream()
				.filter(p -> p.getName().equalsIgnoreCase(name))
				.map(p -> modelMapper.map(p, PersonDto.class))
				.collect(Collectors.toList());
	}

	@Override
	public Iterable<PersonDto> findPersonsBetweenAge(Integer minAge, Integer maxAge) {
		LocalDate from = LocalDate.now().minusYears(maxAge);
		LocalDate to = LocalDate.now().minusYears(minAge);
		return personRepository.findAll()
				.stream()
				.filter(p -> p.getBirthDate().isAfter(from) && p.getBirthDate().isBefore(to))
				.map(p -> modelMapper.map(p, PersonDto.class))
				.collect(Collectors.toList());
	}

	@Override
	public Iterable<CityPopulationDto> getCitiesPopulation() {
		return personRepository.findAll()
				.stream()
				.collect(Collectors.groupingBy(p -> p.getAddress().getCity(), Collectors.counting()))
				.entrySet()
				.stream()
				.map(e -> new CityPopulationDto(e.getKey(), e.getValue()))
				.collect(Collectors.toList());
	}

}
