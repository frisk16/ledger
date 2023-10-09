const displayButton = document.getElementById('display');
const passwordForm = document.forms.editForm.password;

displayButton.addEventListener('change', e => {
  if(e.target.value == 'disable') {
    passwordForm.style.opacity = '0.5';
    passwordForm.style.pointerEvents = 'none';
  } else {
    passwordForm.style.opacity = '1';
    passwordForm.style.pointerEvents = 'auto';
  }
});