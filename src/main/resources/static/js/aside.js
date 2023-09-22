const openMenuBtn = document.getElementById('paymentsOpenMenuBtn');
const closeMenuBtn = document.getElementById('paymentsCloseMenuBtn');
const background = document.querySelector('.background');
const aside = document.querySelector('aside');
let asideWidth = aside.getBoundingClientRect().width;

window.addEventListener('resize', () => {
  if(window.innerWidth > 767) {
    aside.style.transform = 'translateX(0)';
    background.style.opacity = '0';
    background.style.zIndex = '-1';
  } else {
    aside.style.transform = `translate(-${asideWidth}px)`;
  }
});

openMenuBtn.addEventListener('click', e => {
  e.preventDefault();
  background.style.opacity = '0.5';
  background.style.zIndex = '0';
  aside.style.transform = 'translateX(0)';
});

closeMenuBtn.addEventListener('click', e => {
  e.preventDefault();
  background.style.opacity = '0';
  background.style.zIndex = '-1';
  aside.style.transform = `translateX(-${asideWidth}px)`;
})

background.addEventListener('click', () => {
  if(window.innerWidth < 768) {
    background.style.opacity = '0';
    background.style.zIndex = '-1';
    aside.style.transform = `translateX(-${asideWidth}px)`;
  }
});
