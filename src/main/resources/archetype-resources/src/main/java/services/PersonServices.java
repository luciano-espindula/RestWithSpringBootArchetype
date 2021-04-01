#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ${package}.converter.DozerConverter;
import ${package}.data.model.Person;
import ${package}.data.vo.v1.PersonVO;
import ${package}.exception.ResourceNotFoundException;
import ${package}.repository.PersonRepository;

@Service
public class PersonServices {

	@Autowired
	PersonRepository repository;
	
	//LAE - Ver SpringData
	
	public PersonVO create(PersonVO person ) {
		var entity = DozerConverter.parseObject(person,  Person.class);
		var vo = DozerConverter.parseObject(repository.save(entity),  PersonVO.class);
		return vo;
	}
	
//	public List<PersonVO> findAll(Pageable pageable) {
//		var entities = repository.findAll(pageable).getContent();
//		return DozerConverter.parseListObjects(entities, PersonVO.class);
//	}
	
	public Page<PersonVO> findAll(Pageable pageable) {
		var page = repository.findAll(pageable);
		return page.map(this::convertToPersonVO);
	}
	
	private PersonVO convertToPersonVO(Person entity) {
		return DozerConverter.parseObject(entity, PersonVO.class);
	}
	
	public Page<PersonVO> findByName(String firstName, Pageable pageable) {
		var page = repository.findByName(firstName, pageable);
		return page.map(this::convertToPersonVO);
	}
	
	public PersonVO findById(Long id) {
		var entity = getFindById(id);
		return DozerConverter.parseObject(entity, PersonVO.class);
	}

	private Person getFindById(Long id) {
		return repository.findById(id).orElseThrow(
				() -> new ResourceNotFoundException("No records found for this ID"));
	}
	
	public PersonVO update(PersonVO person ) {
		var entity = getFindById(person.getKey());
		entity.setFirstName(person.getFirstName());
		entity.setLastName(person.getLastName());
		entity.setAddress(person.getAddress());
		entity.setGender(person.getGender());
		
		var vo = DozerConverter.parseObject(repository.save(entity), PersonVO.class);
		
		return vo;
	}
	
	@Transactional
	public PersonVO disablePerson(Long id) {
		repository.disablePersons(id);
		
		var entity = getFindById(id);
		return DozerConverter.parseObject(entity, PersonVO.class);
	}
	
	public void delete(Long id) {
		Person entity = getFindById(id);
		
		repository.delete(entity);
	}
	
}
