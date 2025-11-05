
document.addEventListener('DOMContentLoaded', ()=>{
  const searchForm = document.getElementById('search-form');
  if(searchForm){
    searchForm.addEventListener('submit', e=>{
      e.preventDefault();
      const q = document.getElementById('q').value.trim();
      if(!q) return;
      
      console.log('Search for:', q);
      window.location.href = '/search?q=' + encodeURIComponent(q);
    });
  }

  document.querySelectorAll('.add-to-cart-btn').forEach(btn=>{
    btn.addEventListener('click', e=>{
      const id = btn.dataset.id;
      console.log('Add to cart (demo):', id);
      
      btn.textContent = 'Added';
      btn.disabled = true;
    })
  })
});
