document.addEventListener('DOMContentLoaded', () => {
  document.querySelectorAll('[data-cep-form]').forEach((form) => {
    const cepInput = form.querySelector('[data-cep-input]');
    if (!cepInput) {
      return;
    }

    cepInput.addEventListener('blur', async () => {
      const rawCep = cepInput.value.replace(/\D/g, '');
      if (rawCep.length !== 8) {
        return;
      }

      try {
        const response = await fetch(`/api/ceps/${rawCep}`, {
          headers: { Accept: 'application/json' },
        });

        const body = await response.json();
        if (!response.ok) {
          throw new Error(body.message || 'Falha ao consultar o CEP.');
        }

        setValue(form, 'logradouro', body.logradouro);
        setValue(form, 'bairro', body.bairro);
        setValue(form, 'cidade', body.cidade);
        setValue(form, 'uf', body.uf);
      } catch (error) {
        if (window.Swal) {
          Swal.fire({
            icon: 'error',
            title: 'CEP não localizado',
            text: error.message,
            confirmButtonColor: '#b91c1c',
          });
        }
      }
    });
  });
});

function setValue(form, field, value) {
  const input = form.querySelector(`[data-cep-target="${field}"]`);
  if (input) {
    input.value = value || '';
  }
}
