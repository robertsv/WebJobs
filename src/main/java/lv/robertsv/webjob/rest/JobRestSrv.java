package lv.robertsv.webjob.rest;

import java.util.List;
import java.util.stream.Collectors;

import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import lv.robertsv.webjob.domain.JobEntity;
import lv.robertsv.webjob.dto.Job;
import lv.robertsv.webjob.repository.ProductRepository;
import lv.robertsv.webjob.service.ScheduleManager;

@RestController
@RequestMapping("/job")
public class JobRestSrv {

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private ScheduleManager schedulerManager;
	
	@Autowired
	private DozerBeanMapper dozerBeanMapper;

	@RequestMapping(value = "/all", method = RequestMethod.GET)
	public List<Job> getJobs() {
		List<JobEntity> jobs = productRepository.findAll();
		return jobs.stream().map(from -> dozerBeanMapper.map(from, Job.class)).collect(Collectors.toList());
	}

	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public RestSrvResponse addJob(@RequestBody Job job) {
		JobEntity jobEntity = dozerBeanMapper.map(job, JobEntity.class);
		jobEntity = productRepository.save(jobEntity);
		// TODO (RV): not correct
		job.setId(jobEntity.getId());
		schedulerManager.addToSchedule(job);
		// TODO (RV): fix it
		return RestSrvResponse.OK;
	}
	
	@RequestMapping(value = "/delete/{jobId}", method = RequestMethod.POST)
	public RestSrvResponse removeJob(@PathVariable("jobId") Long jobId) {
		schedulerManager.removeFromSchedule(jobId);
		productRepository.delete(jobId);
		// TODO (RV): fix it
		return RestSrvResponse.OK;
	}
	
}