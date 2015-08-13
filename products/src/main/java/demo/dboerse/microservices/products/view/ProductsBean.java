package demo.dboerse.microservices.products.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateful;
import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import demo.dboerse.microservices.products.model.Products;

/**
 * Backing bean for Products entities.
 * <p/>
 * This class provides CRUD functionality for all Products entities. It focuses
 * purely on Java EE 6 standards (e.g. <tt>&#64;ConversationScoped</tt> for
 * state management, <tt>PersistenceContext</tt> for persistence,
 * <tt>CriteriaBuilder</tt> for searches) rather than introducing a CRUD framework or
 * custom base class.
 */

@Named
@Stateful
@ConversationScoped
public class ProductsBean implements Serializable {

	private static final long serialVersionUID = 1L;

	/*
	 * Support creating and retrieving Products entities
	 */

	private Integer id;

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	private Products products;

	public Products getProducts() {
		return this.products;
	}

	public void setProducts(Products products) {
		this.products = products;
	}

	@Inject
	private Conversation conversation;

	@PersistenceContext(unitName = "products-persistence-unit", type = PersistenceContextType.EXTENDED)
	private EntityManager entityManager;

	public String create() {

		this.conversation.begin();
		this.conversation.setTimeout(1800000L);
		return "create?faces-redirect=true";
	}

	public void retrieve() {

		if (FacesContext.getCurrentInstance().isPostback()) {
			return;
		}

		if (this.conversation.isTransient()) {
			this.conversation.begin();
			this.conversation.setTimeout(1800000L);
		}

		if (this.id == null) {
			this.products = this.example;
		} else {
			this.products = findById(getId());
		}
	}

	public Products findById(Integer id) {

		return this.entityManager.find(Products.class, id);
	}

	/*
	 * Support updating and deleting Products entities
	 */

	public String update() {
		this.conversation.end();

		try {
			if (this.id == null) {
				this.entityManager.persist(this.products);
				return "search?faces-redirect=true";
			} else {
				this.entityManager.merge(this.products);
				return "view?faces-redirect=true&id="
						+ this.products.getProductId();
			}
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(e.getMessage()));
			return null;
		}
	}

	public String delete() {
		this.conversation.end();

		try {
			Products deletableEntity = findById(getId());

			this.entityManager.remove(deletableEntity);
			this.entityManager.flush();
			return "search?faces-redirect=true";
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(e.getMessage()));
			return null;
		}
	}

	/*
	 * Support searching Products entities with pagination
	 */

	private int page;
	private long count;
	private List<Products> pageItems;

	private Products example = new Products();

	public int getPage() {
		return this.page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getPageSize() {
		return 10;
	}

	public Products getExample() {
		return this.example;
	}

	public void setExample(Products example) {
		this.example = example;
	}

	public String search() {
		this.page = 0;
		return null;
	}

	public void paginate() {

		CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();

		// Populate this.count

		CriteriaQuery<Long> countCriteria = builder.createQuery(Long.class);
		Root<Products> root = countCriteria.from(Products.class);
		countCriteria = countCriteria.select(builder.count(root)).where(
				getSearchPredicates(root));
		this.count = this.entityManager.createQuery(countCriteria)
				.getSingleResult();

		// Populate this.pageItems

		CriteriaQuery<Products> criteria = builder.createQuery(Products.class);
		root = criteria.from(Products.class);
		TypedQuery<Products> query = this.entityManager.createQuery(criteria
				.select(root).where(getSearchPredicates(root)));
		query.setFirstResult(this.page * getPageSize()).setMaxResults(
				getPageSize());
		this.pageItems = query.getResultList();
	}

	private Predicate[] getSearchPredicates(Root<Products> root) {

		CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();
		List<Predicate> predicatesList = new ArrayList<Predicate>();

		String productName = this.example.getProductName();
		if (productName != null && !"".equals(productName)) {
			predicatesList.add(builder.like(
					builder.lower(root.<String> get("productName")),
					'%' + productName.toLowerCase() + '%'));
		}
		String productDescription = this.example.getProductDescription();
		if (productDescription != null && !"".equals(productDescription)) {
			predicatesList.add(builder.like(
					builder.lower(root.<String> get("productDescription")),
					'%' + productDescription.toLowerCase() + '%'));
		}
		String unit = this.example.getUnit();
		if (unit != null && !"".equals(unit)) {
			predicatesList.add(builder.like(
					builder.lower(root.<String> get("unit")),
					'%' + unit.toLowerCase() + '%'));
		}
		String currency = this.example.getCurrency();
		if (currency != null && !"".equals(currency)) {
			predicatesList.add(builder.like(
					builder.lower(root.<String> get("currency")),
					'%' + currency.toLowerCase() + '%'));
		}

		return predicatesList.toArray(new Predicate[predicatesList.size()]);
	}

	public List<Products> getPageItems() {
		return this.pageItems;
	}

	public long getCount() {
		return this.count;
	}

	/*
	 * Support listing and POSTing back Products entities (e.g. from inside an
	 * HtmlSelectOneMenu)
	 */

	public List<Products> getAll() {

		CriteriaQuery<Products> criteria = this.entityManager
				.getCriteriaBuilder().createQuery(Products.class);
		return this.entityManager.createQuery(
				criteria.select(criteria.from(Products.class))).getResultList();
	}

	@Resource
	private SessionContext sessionContext;

	public Converter getConverter() {

		final ProductsBean ejbProxy = this.sessionContext
				.getBusinessObject(ProductsBean.class);

		return new Converter() {

			@Override
			public Object getAsObject(FacesContext context,
					UIComponent component, String value) {

				return ejbProxy.findById(Integer.valueOf(value));
			}

			@Override
			public String getAsString(FacesContext context,
					UIComponent component, Object value) {

				if (value == null) {
					return "";
				}

				return String.valueOf(((Products) value).getProductId());
			}
		};
	}

	/*
	 * Support adding children to bidirectional, one-to-many tables
	 */

	private Products add = new Products();

	public Products getAdd() {
		return this.add;
	}

	public Products getAdded() {
		Products added = this.add;
		this.add = new Products();
		return added;
	}
}
