import { Component, OnInit, OnDestroy } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { PizzaService } from '../../services/pizza.service';
import { PizzaOrder } from '../../models/pizza-order.model';
import { Subscription } from 'rxjs';

interface PizzaDetail {
  name: string;
  basePrice: number; // in USD
  description: string;
  ingredients: string[];
  emoji: string;
}

@Component({
  selector: 'app-order-pizza',
  templateUrl: './order-pizza.component.html',
  styleUrls: ['./order-pizza.component.css']
})
export class OrderPizzaComponent implements OnInit, OnDestroy {
  orderForm!: FormGroup;
  isSubmitting = false;

  // Custom Toast State
  toast: { message: string, type: 'success' | 'error' | null } = { message: '', type: null };
  private toastTimeoutId: any = null;

  pizzaDetails: PizzaDetail[] = [
    {
      name: 'Margherita',
      basePrice: 12.99,
      description: 'The timeless Neapolitan classic featuring a simple yet rich blend of tomatoes and fresh herbs.',
      ingredients: ['Fresh Mozzarella', 'San Marzano Tomatoes', 'Fresh Basil', 'Extra Virgin Olive Oil'],
      emoji: '🧀'
    },
    {
      name: 'Pepperoni',
      basePrice: 14.99,
      description: "America's favorite pizza topped with loaded double portions of crispy, savory beef pepperoni.",
      ingredients: ['Beef Pepperoni', 'Mozzarella', 'Parmesan', 'Rustic Tomato Sauce', 'Oregano'],
      emoji: '🍕'
    },
    {
      name: 'BBQ Chicken',
      basePrice: 16.49,
      description: 'A perfect sweet-and-savory profile with tender grilled chicken breast smothered in sweet smoky BBQ sauce.',
      ingredients: ['Grilled Chicken Breast', 'Smoky BBQ Sauce', 'Caramelized Red Onions', 'Fresh Cilantro', 'Mozzarella'],
      emoji: '🍗'
    },
    {
      name: 'Hawaiian',
      basePrice: 14.49,
      description: 'The legendary combination of sweet, juicy pineapple chunks offset by savory premium ham slices.',
      ingredients: ['Sweet Pineapple Chunks', 'Smoked Honey Ham', 'Crispy Bacon Bits', 'Mozzarella', 'Tomato Sauce'],
      emoji: '🍍'
    },
    {
      name: 'Veggie Supreme',
      basePrice: 15.49,
      description: 'A colorful, garden-fresh feast packed with a crunchy array of premium roasted vegetables.',
      ingredients: ['Bell Peppers', 'Kalamata Olives', 'Red Onions', 'Cremini Mushrooms', 'Sweet Corn', 'Mozzarella'],
      emoji: '🥦'
    },
    {
      name: 'Meat Lovers',
      basePrice: 17.99,
      description: "A carnivore's dream topped to the absolute brim with six premium seasoned meats.",
      ingredients: ['Pepperoni', 'Italian Sausage', 'Smoked Ham', 'Crispy Bacon', 'Seasoned Ground Beef', 'Mozzarella'],
      emoji: '🥩'
    },
    {
      name: 'Four Cheese',
      basePrice: 14.99,
      description: 'An indulgent, velvety blend of four premium artisanal Italian cheeses with a touch of fresh garlic.',
      ingredients: ['Mozzarella', 'Gorgonzola', 'Parmesan', 'Creamy Ricotta', 'Minced Garlic', 'Olive Oil Drizzle'],
      emoji: '🧀'
    },
    {
      name: 'Mushroom Truffle',
      basePrice: 18.49,
      description: 'An elegant, gourmet white pizza finished with earthy wild mushrooms and luxurious black truffle oil.',
      ingredients: ['Wild Cremini & Oyster Mushrooms', 'Luxurious Black Truffle Oil', 'Caramelized Onions', 'Fresh Arugula', 'Mozzarella'],
      emoji: '🍄'
    }
  ];

  famousPizzas = this.pizzaDetails.map(p => p.name);
  selectedPizza: PizzaDetail | null = null;

  // Real-time Exchange Rates
  selectedCurrency = 'INR';
  exchangeRates: { [key: string]: number } = { 'INR': 83.50, 'USD': 1.0 };
  supportedCurrencies = ['INR'];
  currencySymbols: { [key: string]: string } = {
    'INR': '₹'
  };

  private subscription = new Subscription();

  constructor(private fb: FormBuilder, private pizzaService: PizzaService) {}

  ngOnInit(): void {
    this.orderForm = this.fb.group({
      pizzaName: ['', Validators.required],
      quantity: [1, [Validators.required, Validators.min(1)]],
      bill: ['', Validators.required],
      customerContactNumber: ['', [
        Validators.required, 
        Validators.pattern('^[0-9]{10}$'), // Exactly 10 digits
        Validators.minLength(10), 
        Validators.maxLength(10)
      ]]
    });

    // Fetch real-time exchange rates
    this.loadExchangeRates();

    // Listen to form value changes to calculate bill dynamically
    this.subscription.add(
      this.orderForm.valueChanges.subscribe(() => {
        this.updateBill();
      })
    );
  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
    if (this.toastTimeoutId) {
      clearTimeout(this.toastTimeoutId);
    }
  }

  loadExchangeRates(): void {
    this.pizzaService.getExchangeRates().subscribe({
      next: (data) => {
        if (data && data.rates && data.rates['INR']) {
          this.exchangeRates['INR'] = data.rates['INR'];
          this.updateBill();
        }
      },
      error: (err) => {
        console.warn('Failed to fetch real-time exchange rates, using fallback values.', err);
        this.exchangeRates = {
          'INR': 83.50,
          'USD': 1.0
        };
      }
    });
  }

  onCurrencyChange(currency: string): void {
    this.selectedCurrency = currency;
    this.updateBill();
  }

  updateBill() {
    if (!this.orderForm) return;
    const pizzaName = this.orderForm.get('pizzaName')?.value;
    const qty = this.orderForm.get('quantity')?.value;
    
    this.selectedPizza = this.pizzaDetails.find(p => p.name === pizzaName) || null;
    
    if (this.selectedPizza && qty && qty >= 1) {
      const baseCostUSD = this.selectedPizza.basePrice * qty;
      const rate = this.exchangeRates[this.selectedCurrency] || 1;
      const finalCost = parseFloat((baseCostUSD * rate).toFixed(2));
      
      this.orderForm.patchValue({ bill: finalCost }, { emitEvent: false });
    } else {
      this.orderForm.patchValue({ bill: '' }, { emitEvent: false });
    }
  }

  showToast(message: string, type: 'success' | 'error'): void {
    if (this.toastTimeoutId) {
      clearTimeout(this.toastTimeoutId);
    }
    this.toast = { message, type };
    this.toastTimeoutId = setTimeout(() => {
      this.toast = { message: '', type: null };
    }, 4000);
  }

  onSubmit(): void {
    if (this.orderForm.invalid) {
      this.orderForm.markAllAsTouched();
      return;
    }

    this.isSubmitting = true;

    // Create a copy of the order to send to the backend
    const formValue = this.orderForm.value;
    
    const orderData: PizzaOrder = {
      pizzaName: formValue.pizzaName,
      quantity: formValue.quantity,
      bill: formValue.bill,
      customerContactNumber: formValue.customerContactNumber
    };

    this.pizzaService.addPizza(orderData).subscribe({
      next: (response) => {
        this.showToast(response || 'Pizza ordered successfully!', 'success');
        this.isSubmitting = false;
        this.orderForm.reset({ quantity: 1 });
        this.selectedPizza = null;
      },
      error: (err) => {
        this.showToast(err.error || 'Failed to place order. Please try again.', 'error');
        this.isSubmitting = false;
      }
    });
  }
}
