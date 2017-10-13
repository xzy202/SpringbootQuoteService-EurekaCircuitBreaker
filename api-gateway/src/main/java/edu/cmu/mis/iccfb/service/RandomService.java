package edu.cmu.mis.iccfb.service;

import org.springframework.beans.factory.annotation.Autowired;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import edu.cmu.mis.iccfb.model.Author;
import edu.cmu.mis.iccfb.model.Quote;
import edu.cmu.mis.iccfb.model.QuoteFormat;
import edu.cmu.mis.iccfb.model.AuthorFormat;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

@Service
public class RandomService {

	
    @LoadBalanced
    @Autowired
    private RestTemplate QuoterestTemplate;
    
    String QuoteUrl = "http://quote-service";
    
    @LoadBalanced
    @Autowired
    private RestTemplate AuthorrestTemplate;
    
    String AuthorUrl = "http://author-service";
    
    @HystrixCommand(fallbackMethod = "reliable")  
    public QuoteFormat random() {

      Quote response = QuoterestTemplate.getForObject(QuoteUrl+"/quote/randomquote",Quote.class);

      String text = response.getText();
      String source = response.getSource();
      Long authorId = response.getAuthorId();


      Author author = AuthorrestTemplate.getForObject(AuthorUrl+"/author/"+Long.toString(authorId) , Author.class);

      Long id = author.getId();
      String name = author.getName();

      AuthorFormat new_author = new AuthorFormat(name);
      new_author.setId(id);
      QuoteFormat quote = new QuoteFormat(text,source,new_author);
      return quote;
    }
    
    	public QuoteFormat reliable() {
    		AuthorFormat author = new AuthorFormat("Mark Twain");
    		String quote = "The secret of getting ahead is getting started.";
    		String source = "https://www.brainyquote.com/quotes/quotes/m/marktwain118964.html";
    return new QuoteFormat(quote,source,author);
  }

}